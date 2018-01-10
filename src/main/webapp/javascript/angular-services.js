/**
 * Content-Type': 'application/x-www-form-urlencoded/multipart/form-data
 */

myApp.factory('APIService', ['$http', function($http) {

	var path = location.pathname;
	var appName = path.split('/')[1];
    return {
        doApiCall: function(obj){
            var xhr = $http({
                url: 'https://' + location.host + '/rest/UserService/' + obj.req_name,
                method: 'POST',
                timeout: obj.timeout,
                params: obj.params,
                headers: {'Content-Type': 'multipart/form-data'}
            });

            return xhr;
        },
        doJsonApiCall: function(obj){

            var xhr = $http({
                url: obj.req_name,
                method: 'POST',
                timeout: obj.timeout,
                data: obj.params
            });

            return xhr;
        }
    };
}]);

myApp.service('fileUploadService', function ($http, $q) {
	 
    this.uploadFileToUrl = function (file, uploadUrl) {
        //FormData, object of key/value pair for form fields and values

        var deffered = $q.defer();
        $http.post(uploadUrl, fileFormData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}

        }).success(function (response) {
            deffered.resolve(response);

        }).error(function (response) {
            deffered.reject(response);
        });

        return deffered.promise;
    }
});


/*APIService.doApiCall({
    "req_name": "getProductConfigDetail",
    "params":{"productId": productId}
});*/



myApp.factory('authInterceptor', function () {
    return {
        request: function (config) {
            config.headers = config.headers || {};

            if(config.url.startsWith("uib/template")
                    || config.url.startsWith("tab/tab")){
                //Not modifying requests to these urls, 
                //as they are angular template cache requests
                return config;
            }else{
                //Do the interceptor work here. Add headers or whatever.
                return config;
            }
        }
    };
});

myApp.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
});