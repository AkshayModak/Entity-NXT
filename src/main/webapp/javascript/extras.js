
	$scope.nestedEmployees = nestedEmployees;
	
	$scope.countryLikes = function(employee) {
		employee.likes++;
	};
	
	$scope.countryDislikes = function(employee) {
	    if(employee.likes > 0) {
	        employee.likes--;
	    }
	};
	
	$scope.saveData = saveData;
		var employee = {
		name: 'Akshay',
		age: '24',
		city: 'Indore',
		country: 'India',
		flagUrl: 'images/india-flag.png'
	};
	
	$scope.employee = employee;
	
	var employees= [
	{
		name: 'Akshay',
		age: '24',
        gender: 1,
		city: 'Indore',
		country: 'India',
		likes: '0',
		dislikes: '0',
        salary: '70000'
	},
	{
		name: 'Amit',
		age: '42',
        gender: 1,
		city: 'Ujjain',
		country: 'India',
		likes: '0',
		dislikes: '0',
        salary: '68000'
	},
	{
		name: 'David',
		age: '31',
        gender: 1,
		city: 'San fransico',
		country: 'United States of America',
		likes: '0',
		dislikes: '0',
        salary: '65000'
	},
	{
		name: 'Sara',
		age: '45',
        gender: 2,
		city: 'London',
		country: 'United Kingdom',
		likes: '0',
		dislikes: '0',
        salary: '60000'
	},
	{
		name: 'Jackie',
		age: '59',
        gender: 3,
		city: 'Beijing',
		country: 'China',
		likes: '0',
		dislikes: '0',
        salary: '63400'
	},
	{
		name: 'Ricky',
		age: '39',
        gender: 1,
		city: 'Sydney',
		country: 'Australia',
		likes: '0',
		dislikes: '0',
        salary: '73000'
	}];
	
	$scope.employees = employees;
    $scope.sortColumn = "name";
	
	
	
    $scope.employee = employee;
	
	var nestedEmployees= [
	{
		name: 'Akshay',
		age: '24',
		cities: [
		    {
			    name: 'Indore'
		    },
		    {
			    name: 'Dewas'
		    },
		    {
			    name: 'Ujjain'
		    },
		    {
			    name: 'Jaipur'
		    },
		],
		country: 'India'
	},
	{
		name: 'Amit',
		age: '42',
		cities: [
		    {
			    name: 'Mumbai'
		    },
		    {
			    name: 'Delhi'
		    },
		    {
			    name: 'Chennai'
		    },
		    {
			    name: 'Kolkata'
		    },
		],
		country: 'India'
	},
	{
		name: 'David',
		age: '31',
		cities: [
		    {
			    name: 'New York City'
		    },
		    {
			    name: 'San francisco'
		    },
		    {
			    name: 'Washington D.C.'
		    },
		    {
			    name: 'Los Angeles'
		    },
		],
		country: 'United States of America'
	},
	{
		name: 'Sara',
		age: '45',
		cities: [
		    {
			    name: 'London'
		    },
		    {
			    name: 'Birmingham'
		    },
		    {
			    name: 'Liverpoor'
		    },
		    {
			    name: 'Bristol'
		    },
		],
		country: 'United Kingdom'
	},
	{
		name: 'Jackie',
		age: '59',
		cities: [
		    {
			    name: 'Shenghai'
		    },
		    {
			    name: 'Beijing'
		    },
		    {
			    name: 'Chengdu'
		    },
		    {
			    name: 'Nanjing'
		    },
		],
		country: 'China'
	},
	{
		name: 'Ricky',
		age: '39',
		cities: [{name: 'Sydney'},{name: 'Perth'},{name: 'Melbourne'},{name: 'Adelaide'}],
		country: 'Australia'
	}
	];