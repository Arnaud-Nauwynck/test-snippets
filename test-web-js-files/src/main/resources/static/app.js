'use strict';
angular.module('myapp', [ 'draganddrop' ])
.directive("setModelOnChange", function() {
  return {
    require: "ngModel",
    link: function postLink(scope,elem,attrs,ngModel) {
      elem.on("change", function(e) {
        console.log("on change (from directive)", e);  
        var files = elem[0].files;
        ngModel.$setViewValue(files);
      })
    }
  }
})
/*
.directive("myappDragstart", function() {
    return {
        restrict: 'A',
        scope: {
            dragstart: '&'
        },
        link: function (scope, element, attrs) {
          var domElement = element[0];
          var callbackExpr = attrs.myappDragstart;
          console.log("directive myapp-dragstart attrs", attrs);
          domElement.addEventListener('dragstart', function (e) {
                console.log("event dragstart");
                scope.dragstart(e);
          })
        }
    }
})
*/
.controller('MyController', function($scope, $http) {
    var self = this;
    self.selectedFiles1 = []; // from input
    self.selectedFiles2 = []; // from drag&drop
    self.selectedFileWithContents = [];
    self.uploadStatus = '';
    
    self.onInputFileChange = function() {
      console.log("onInputFileChange: selected files: ", self.selectedFiles1);
    };
    
    self.addSelectedFiles2 = function(files) {
      console.log("puree js event -> angular callback addSelectedFiles2", files);
    }
    
    // Read file content
    // ------------------------------------------------------------------------
    self.onReadSelectedFiles = function() {
      self.selectedFileWithContents = [];
      var files = [...self.selectedFiles1, ...self.selectedFiles2];
      for(var i = 0; i < files.length; i++) {
          var file =  files[i];
          console.log("async read file[" + i + "]", file);
          
          var reader = new FileReader();
          reader.onload = (function(f) {
            return function(e) {
                $scope.$apply(function() { // ugly.. force angular refresh!! 
                  var textContent = e.target.result;
                  console.log("finished read file[" + i + "]",{ name: f.name, textContent: textContent});
                  self.selectedFileWithContents.push({ name: f.name, textContent: textContent});
                });
            };
          })(file);
          reader.readAsText(file);
          
      }
    };
    

    // Drag'n Drop using native js event
    // ------------------------------------------------------------------------
    
    function handleDrop(evt) {
        evt.stopPropagation();
        evt.preventDefault();

        var files = evt.dataTransfer.files; // FileList object.
        console.log("dropped files:", files);
    }

      function handleDragOver(evt) {
        evt.stopPropagation();
        evt.preventDefault();
        evt.dataTransfer.dropEffect = 'copy';
      }
    
//      // Setup the dnd listeners.
//      var dropZone = document.getElementById('drop_zone');
//      dropZone.addEventListener('dragover', handleDragOver, false);
//      dropZone.addEventListener('drop', handleDrop, false);   

      
      
      // Drag'n Drop using PATCHED angular-draganddrop.js module
      // ------------------------------------------------------------------------

      self.onDragOver = function($event) {
          var files = $event.dataTransfer.files;
          // console.log("onDragOver (using draganddrop.js module directive)", {event: $event, files: files} );  
      };
      
      self.dropAccept = function($event) {
          // console.log("dropAccept (using draganddrop.js module directive)", $event);
          var files = $event.dataTransfer.files;
          // cf 'text/plain' ...
          return true; // temporary for test anyway..
      }
      self.onDrop = function(data, $event) {
          var files = $event.dataTransfer.files;
          console.log("onDrop (using draganddrop.js module directive)", {data, $event, files});
          // self.selectedFiles2.push(...files); do not add doublon..
          for(var i = 0; i < files.length; i++) {
              var f = files[i];
              if (-1 === findFileElt(self.selectedFiles2, f)) { // indexOf() does not work!
                  self.selectedFiles2.push(f);
              } else {
                  console.log('file already added ', f);
              }
          }
      };
      
      function findFileElt(ls, elt) {
          for(var i = 0; i < ls.length; i++) {
             if (ls[i].name === elt.name && // can not compare by full path?!!
             ls[i].size === elt.size && ls[i].lastModified === elt.lastModified) {
                 return i;
             }   
          }
          return -1;
      }
      
      // $http uploadFiles
      // ------------------------------------------------------------------------
      
      self.onUploadFilesDatas = function() {
          var files = [ ...self.selectedFiles1, ...self.selectedFiles2 ];
          for (var i = 0; i < files.length; i++) {
              var file = files[i];
              var fileName = file.name;
              console.log("async read file[" + i + "]: '" + fileName + "'", file);
              var reader = new FileReader();
              reader.onload = (function(f) {
                  return function(e) {
                      var bytesContent = Array.from(new Int8Array(e.target.result));
                      // => async 1 by 1 calls ... may use Promise.all to perform only 1 asyncUploadDatas()
                      self.asyncUploadDatas(f.name, bytesContent);
                  };
              })(file);
              reader.readAsArrayBuffer(file);
          }
      };

      self.asyncUploadDatas = function(fileName, bytesContent) {
          var req = [{ name: fileName, content: bytesContent }];
          $http.post('/app/uploadDatas', req)
          .then(function(res) {
             self.uploadStatus += 'OK uploaded ' + fileName + '\n';               
          }, function(err) {
             self.uploadStatus += 'Failed uploaded ' + fileName + ' : ' + err + '\n';
          });
      };
      
      
      self.onUploadFilesMultipartFiles = function() {
        var files = [ ...self.selectedFiles1, ...self.selectedFiles2 ];
        var formData = new FormData();
        for(var i = 0; i < files.length; i++) {
          formData.append(files[i].name, files[i]);
        }
        $http.post('/app/uploadMultipartFiles', formData, {
          transformRequest: angular.identity,
          headers: { 'Content-Type': undefined }
        }).then(function (data) {
           self.uploadStatus += 'OK uploaded ' + files + ' using MultipartFile\n';            
        }, function (err) {
           self.uploadStatus += 'Failed uploaded ' + files + ' using MultipartFile' + err + '\n';            
        });
      };
      
      
      self.onUploadFilesStream = function() {
        var files = [ ...self.selectedFiles1, ...self.selectedFiles2 ];
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var fileName = file.name;
            console.log("async read file[" + i + "]: '" + fileName + "'", file);
            var reader = new FileReader();
            reader.onload = (function(f) {
                return function(e) {
                    var bytesContent = new Int8Array(e.target.result);
                    self.asyncUploadFileStream(f.name, bytesContent);                    
                };
            })(file);
            reader.readAsArrayBuffer(file);
        }
      };

      self.asyncUploadFileStream = function(fileName, int8ArrayContent) {
          $http.post('/app/uploadFileStream/' + fileName, int8ArrayContent, {
              transformRequest: [],
              headers: { 'Content-Type': undefined }
          })
          .then(function(res) {
             self.uploadStatus += 'OK uploaded ' + fileName + ' using srv streams\n';
          }, function(err) {
             self.uploadStatus += 'Failed uploaded ' + fileName + ' using srv streams : ' + err + '\n';
          });
      };

      self.onUploadFileBytes = function() {
          var files = [ ...self.selectedFiles1, ...self.selectedFiles2 ];
          for (var i = 0; i < files.length; i++) {
              var file = files[i];
              var fileName = file.name;
              console.log("async read file[" + i + "]: '" + fileName + "'", file);
              var reader = new FileReader();
              reader.onload = (function(f) {
                  return function(e) {
                      var bytesContent = new Int8Array(e.target.result);
                      self.asyncUploadFileBytes(f.name, bytesContent);                    
                  };
              })(file);
              reader.readAsArrayBuffer(file);
          }
      };

      self.asyncUploadFileBytes = function(fileName, int8ArrayContent) {
          $http.post('/app/uploadFileBytes/' + fileName, int8ArrayContent, {
              transformRequest: [],
              headers: { 'Content-Type': undefined }
          }).then(function(res) {
             self.uploadStatus += 'OK uploaded ' + fileName + '\n';               
          }, function(err) {
             self.uploadStatus += 'Failed uploaded ' + fileName + ' : ' + err + '\n';
          });
      };
      
      
});
