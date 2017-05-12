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
.controller('MyController', function($scope) {
    var self = this;
    self.selectedFiles1 = []; // from input
    self.selectedFiles2 = []; // from drag&drop
    self.selectedFileWithContents = [];
    
    self.onInputFileChange = function(e) {
      console.log("onInputFileChange ", e);
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
});
