
let object = {
	name: 'arnaud',
	skills: [ 'IT', 'math']
};
console.log(`Hello ${object.name}`);

// JS untyped: Any: map<String,Any>
let anotherObj: any = new Object();
// anotherObj.prototype ... JS dark-magic
anotherObj.name = 'fabien';
anotherObj['skills'] = [ 'IT', 'other' ];
 




