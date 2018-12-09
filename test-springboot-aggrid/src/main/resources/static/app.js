agGrid.initialiseAgGridWithAngular1(angular);

var module = angular.module("app", ["agGrid"]);

module.controller("todoCtrl", function($http) {
	var self = this;
	self.rows = [];

    var columnDefs = [
        {headerName: "Task", field: "task"},
        {headerName: "Comment", field: "comment"},
        {headerName: "Date", field: "date"}
    ];

    self.gridOptions = {
        columnDefs: columnDefs,
        rowData: self.rows
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