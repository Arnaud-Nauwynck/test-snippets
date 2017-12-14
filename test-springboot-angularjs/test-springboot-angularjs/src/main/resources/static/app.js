angular.module("my-app", []);

angular.module("my-app")
.controller("MyController", function($http) {
	var self = this;
	self.todoMessage = "";
	self.todos = [];
	
	self.addTodo = function() {
		// self.todos.push({ todoMessage: self.todoMessage });
		var req = {
				todoMessage: self.todoMessage
		};
		$http.post('/api/todo', req).then(function(resp,status) {
			// reload all
			self.loadTodos();
		}, function(resp,status) {
			console.log('Error ' + resp);
		})
	};
	
	self.loadTodos = function() {
		$http.get('/api/todo').then(function(resp,status) {
			self.todos = resp.data;
			console.log('data', self.todos);
		}, function(resp,status) {
			console.log('Error ' + resp);
		})
	};

	
	
	self.loadTodos();
});