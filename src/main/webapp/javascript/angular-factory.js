myApp.factory('ModalService', function($uibModal) {
    return {
        openTextEditModal: function(item) {
            var modalInstance = $uibModal.open({
                templateUrl: '/final/trailerModal.html',
                backdrop: 'static',
                controller: function($scope, $modalInstance, $sce, item) {
                    var clone = {};
                    angular.copy(item, clone);
                    $scope.clone = clone;
                    $scope.close = function() {
                        $modalInstance.dismiss('cancel');
                    };
                    $scope.save = function() {
                      angular.extend(item, clone);
                      $modalInstance.close();
                    };
                },
                size: 'lg',
                resolve: {
                    item: function() {
                        return item;
                    }
                }
            });
        }
    };
});