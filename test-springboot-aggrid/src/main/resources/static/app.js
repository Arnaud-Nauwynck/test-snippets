agGrid.initialiseAgGridWithAngular1(angular);

var module = angular.module("app", ["agGrid"]);

module.controller("todoCtrl", function($http) {
	var self = this;
	self.rows = [];
	self.task = '';
	self.comment = '';

    var columnDefs = [
        {headerName: "Task", field: "task"},
        {headerName: "Comment", field: "comment"},
        {headerName: "Date", field: "date"}
    ];

    self.gridOptions = {
        columnDefs: columnDefs,
        rowData: self.rows
    };


	self.addTodo = function() {
		// self.todos.push({ todoMessage: self.todoMessage });
		var req = {
			task: self.task,
			comment: self.comment
		};
		$http.post('/api/todo', req).then(function(resp,status) {
			// reload all
			self.loadAsync();
		}, function(resp,status) {
			console.log('Error ' + resp);
		})
	};
	
	
    self.loadAsync = function() {
    	$http.get("/api/todo").then(function(resp) {
    		self.rows = resp.data;
    		console.log("refreshing.. " + self.rows.length);
    		self.gridOptions.api.setRowData(self.rows)
    		// self.gridOptions.api.refreshCells();
    	}, function(err) {
    		console.log("Failed " + err);
    	});
    };
    
});
