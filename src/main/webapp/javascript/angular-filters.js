// filter to reverse angularjs list
// Reference -- https://stackoverflow.com/questions/15266671/angular-ng-repeat-in-reverse
myApp.filter('reverse', function() {
  return function(items) {
	if (!items || !items.length) { return; }
    return items.slice().reverse();
  };
});