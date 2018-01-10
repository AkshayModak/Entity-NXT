
var myApp = angular.module("myModule", ["ui.router",'ngAnimate', 'ngSanitize', "ui.bootstrap"]);

/*myApp.run(function($rootScope, $templateCache) {
	$rootScope.$on('$viewContentLoaded', function() {
		$templateCache.removeAll();
	});
});
*/
myApp.controller('navbarController', function ($scope) {
	  $scope.isNavCollapsed = true;
	  $scope.isCollapsed = false;
	  $scope.isCollapsedHorizontal = false;
});



myApp.controller('CarouselDemoCtrl', function ($scope) {

  var slides = [{
	  imagePath: 'images/carouselImages/tronCarousel.jpg',
	  alt: 'Tron',
	  id: '0'
  }, {
	  imagePath: 'images/carouselImages/avengersCarousel.jpg',
	  alt: 'Avengers',
	  id: '1'
  }, {
	  imagePath: 'images/carouselImages/sherlockCarousel.jpg',
	  alt: 'Sherlock',
	  id: '2'
  }, {
	  imagePath: 'images/carouselImages/bourneCarousel.jpg',
	  alt: 'The Bourne Ultimatum',
	  id: '3'
  }]
  
  $scope.slides = slides;
  console.log(slides);
  
  /*$scope.getCarouselImage = */
});


var homeController = function($scope, APIService, $http) {

	$http({
        method : "POST",
        url : "http://localhost:8080/RestCheck/rest/UserService/getDefaultImagePath"
    }).then(function mySucces(response) {
        $scope.defaultImages = response.data;
        /*$location.path("movieDetails");*/
    }, function myError(response) {
        $scope.defaultImages = response.statusText;
    });
	
	
	APIService.doApiCall({
		"req_name": "getImages",
		"params": {},
	}).success(function(data) {
		$scope.thumbnailImages = data;
	});
}

var movieDetailsController = function($scope, APIService, $stateParams, $uibModal, $log, $document) {
	
	$scope.header = "Movie Details";
	
	APIService.doApiCall({
		"req_name": "getMovieDetails", 
		"params": {"movieId": $stateParams.movieId}
	}).success(function(response) {
		$scope.alerts = [{ 
		    type: 'success', 
		    msg: 'Welcome to '+response.movieName+' page.'
		}];

		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		};
		$scope.movieDetails = response;
	}).error(function(response) {
		$location.path('/home');
	});
}

var showImageController = function($scope, APIService) {
	
	$scope.fullImage = function(imagePath, movieName) {
		APIService.doApiCall({
			"req_name": "showFullImage",
			"params": {},
		}).success(function(data) {
			console.log(data);
			$scope.fullImage = data;
		});
	}
}

var galleryController = function($scope, APIService, $stateParams) {
	$scope.header = "Gallery";

	console.log("=====Inside galleryController=========="+$stateParams);
	console.log($stateParams);
	APIService.doApiCall({
		"req_name": "getGalleryImages", 
		"params": {"movieId": $stateParams.movieId}
	}).success(function(response) {
		$scope.galleryImages = response;
	}).error(function(response) {
		$location.path('/home');
	});
}

var reviewsController = function($scope, APIService) {
	$scope.header = "Reviews";
}

myApp.controller("homeController", homeController);
myApp.controller("movieDetailsController", movieDetailsController);
myApp.controller("galleryController", galleryController);
myApp.controller("reviewsController", reviewsController);
myApp.controller("showImageController", showImageController);

myApp.config(function($stateProvider) {
	$stateProvider.state("home", {
		url: "/home",
		templateUrl: "index.html",
	}).state("movieDetails", {
		url: "/movieDetails",
		templateUrl: "final/Inside.html",
	}).state("reviews", {
		url: "/reviews",
		templateUrl: "templates/reviews.html",
		controller: "reviewsController"
	}).state("gallery", {
		url: "/gallery/:movieId",
		templateUrl: "templates/gallery.html",
		controller: "galleryController"
	}).state("otherwise", {
		url: "/otherwise",
		templateUrl: "templates/home.html",
		controller: "homeController"
	});
});



myApp.controller('ModalDemoCtrl', function ($uibModal, $log, $document) {
	  var $ctrl = this;
	  $ctrl.items = ['item1', 'item2', 'item3'];

	  $ctrl.animationsEnabled = true;

	  $ctrl.open = function (size, parentSelector) {
	    var parentElem = parentSelector ? 
	      angular.element($document[0].querySelector('.modal-demo ' + parentSelector)) : undefined;
	    var modalInstance = $uibModal.open({
	      animation: $ctrl.animationsEnabled,
	      ariaLabelledBy: 'modal-title',
	      ariaDescribedBy: 'modal-body',
	      templateUrl: 'myModalContent.html',
	      controller: 'ModalInstanceCtrl',
	      controllerAs: '$ctrl',
	      size: size,
	      appendTo: parentElem,
	      resolve: {
	        items: function () {
	          return $ctrl.items;
	        }
	      }
	    });

	    modalInstance.result.then(function (selectedItem) {
	      $ctrl.selected = selectedItem;
	    }, function () {
	      $log.info('Modal dismissed at: ' + new Date());
	    });
	  };

	  $ctrl.openComponentModal = function () {
	    var modalInstance = $uibModal.open({
	      animation: $ctrl.animationsEnabled,
	      component: 'modalComponent',
	      resolve: {
	        items: function () {
	          return $ctrl.items;
	        }
	      }
	    });

	    modalInstance.result.then(function (selectedItem) {
	      $ctrl.selected = selectedItem;
	    }, function () {
	      $log.info('modal-component dismissed at: ' + new Date());
	    });
	  };

	  $ctrl.openMultipleModals = function () {
	    $uibModal.open({
	      animation: $ctrl.animationsEnabled,
	      ariaLabelledBy: 'modal-title-bottom',
	      ariaDescribedBy: 'modal-body-bottom',
	      templateUrl: 'stackedModal.html',
	      size: 'sm',
	      controller: function($scope) {
	        $scope.name = 'bottom';  
	      }
	    });

	    $uibModal.open({
	      animation: $ctrl.animationsEnabled,
	      ariaLabelledBy: 'modal-title-top',
	      ariaDescribedBy: 'modal-body-top',
	      templateUrl: 'stackedModal.html',
	      size: 'sm',
	      controller: function($scope) {
	        $scope.name = 'top';  
	      }
	    });
	  };

	  $ctrl.toggleAnimation = function () {
	    $ctrl.animationsEnabled = !$ctrl.animationsEnabled;
	  };
	});

	// Please note that $uibModalInstance represents a modal window (instance) dependency.
	// It is not the same as the $uibModal service used above.

myApp.controller('ModalInstanceCtrl', function ($uibModalInstance, items) {
	var $ctrl = this;
	$ctrl.items = items;
	$ctrl.selected = {
	  item: $ctrl.items[0]
	};
	
	$ctrl.ok = function () {
		$uibModalInstance.close($ctrl.selected.item);
	};

	$ctrl.cancel = function () {
	  $uibModalInstance.dismiss('cancel');
	};
});

	// Please note that the close and dismiss bindings are from $uibModalInstance.

myApp.component('modalComponent', {
	  templateUrl: 'myModalContent.html',
	  bindings: {
	    resolve: '<',
	    close: '&',
	    dismiss: '&'
	  },
	  controller: function () {
	    var $ctrl = this;

	    $ctrl.$onInit = function () {
	      $ctrl.items = $ctrl.resolve.items;
	      $ctrl.selected = {
	        item: $ctrl.items[0]
	      };
	    };

	    $ctrl.ok = function () {
	      $ctrl.close({$value: $ctrl.selected.item});
	    };

	    $ctrl.cancel = function () {
	      $ctrl.dismiss({$value: 'cancel'});
	    };
	  }
});