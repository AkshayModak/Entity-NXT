var myApp = angular.module("myModule", ["ui.router", 'chart.js',
        "ui.bootstrap", "ngMaterial", "infinite-scroll", "LocalStorageModule"]);

/*
 * myApp.config(function (ChartJsProvider) { // Configure all charts
 * ChartJsProvider.setOptions({ colors: ['#97BBCD', '#DCDCDC', '#F7464A',
 * '#46BFBD', '#FDB45C', '#949FB1', '#4D5360'] }); // Configure all doughnut
 * charts ChartJsProvider.setOptions('doughnut', { cutoutPercentage: 60 });
 * ChartJsProvider.setOptions('bubble', { tooltips: { enabled: false } }); });
 */

myApp.controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, content, $sce, AdminAPIService) {
    $scope.close = function () {
        $uibModalInstance.dismiss('cancel');
    };
    $scope.trustSrc = function(trailer) {
        return $sce.trustAsResourceUrl(trailer);
    }
    $scope.content = content;

    $scope.manipulateContent = function(content) {
        console.log(content.content_id);
        if (content != null && content != undefined && content.content_id != null && content.content_id != undefined && content.content_id != "") {
            AdminAPIService
            .doApiCall({
                "req_name" : "updateContent",
                "params" : {"contentId": content.content_id, "screen": content.screen_content, "contentType": content.content_type, 
                    "description": content.description, "electronicText": content.electronicText}
            }).success(
                function(data) {
                    $scope.close();
                    getContent();
            });
        } else {
            AdminAPIService
            .doApiCall({
                "req_name" : "createContent",
                "params" : {"screen": content.screen_content, "contentType": content.content_type, "description": content.description, 
                    "electronicText": content.electronicText}
            }).success(
                function(data) {
                    $scope.close();
                    getContent();
            });
        }
    }

    $scope.removeContent = function(content) {
        AdminAPIService
        .doApiCall({
            "req_name" : "removeContent",
            "params" : {"contentId": content.content_id}
        }).success(
            function(data) {
                $scope.close();
        });
    }
});

var loginController = function ($scope, $rootScope, $location, AuthenticationService, localStorageService) {
            // reset login status
            /*AuthenticationService.ClearCredentials();*/

            $scope.login = function () {
                $scope.dataLoading = true;
                AuthenticationService.Login($scope.username, $scope.password, function(response) {
                    if(response.success) {
                        AuthenticationService.SetCredentials($scope.username, $scope.password);
                        $location.path('/');
                        location.reload();
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            };
            localStorageService.set("name", "akshay");
}

var userMessagesController = function ($scope, $sce, AdminAPIService, $rootScope, $window) {
    $scope.getUserMessages = function() {
        AdminAPIService
        .doApiCall({
            "req_name" : "getUserMessages",
            "params" : {}
        }).success(
            function(data) {
                for (i =0 ; i < data.length; i++) {
                    data[i].messageDateTime = new Date(data[i].messageDateTime);
                }
                $scope.showMessageList = true;
                $scope.userMessages = data;
        });
    }
    $scope.getUserMessages();

    $scope.getMessageContent = function(userMessage) {
        $scope.showMessageList = false;
        $scope.userMessage = userMessage;
        $scope.subject = "No Subject";
        AdminAPIService
        .doApiCall({
            "req_name" : "markMessageRead",
            "params" : {"id": userMessage.user_message_id}
        }).success(
            function(data) {
                unreadMessagesCount();
                $rootScope.unreadMessagesCount = $window.localStorage.getItem("unreadMessagesCount");
            });
    }

    $scope.removeUserMessage = function(userMessage) {
        if (confirm('Are you sure you want remove this message?')) {
            AdminAPIService
            .doApiCall({
                "req_name" : "removeUserMessage",
                "params" : {"id": userMessage.user_message_id}
            }).success(
                function(data) {
                    $scope.getUserMessages();
                    unreadMessagesCount();
            });
        } else {
            // Do nothing!
        }
    }

    unreadMessagesCount = function() {
        AdminAPIService
        .doApiCall({
            "req_name" : "getUnreadMessagesCount",
            "params" : {}
        })
        .success(
                function(data) {
                    $window.localStorage.setItem("unreadMessagesCount", data);
                    $rootScope.unreadMessagesCount = $window.localStorage.getItem("unreadMessagesCount");
                });
    }

    $scope.setDeleteMessage = function(userMessage) {
        $scope.selectedToDelete = userMessage;
    }
    $rootScope.unreadMessagesCount = $window.localStorage.getItem("unreadMessagesCount");
}

//myApp.controller("dashboardController", dashboardController);
myApp.controller("dashboardController" ,function($scope, $rootScope, $uibModal, AdminAPIService, $window, $timeout, localStorageService) {

    $rootScope.home = true;
    $rootScope.pageTitle = "Dashboard | Nextrr";

    getVisits = function() {
        AdminAPIService
                .doApiCall({
                    "req_name" : "getVisits",
                    "params" : {}
                })
                .success(
                        function(data) {
                            data.result = data.result.reverse();
                            $scope.visits = data.result;
                            $scope.visitsAnalysis = data.visitsAnalysis;

                            getPagination("visits", $scope);
                            getPagination("visitsAnalysis", $scope);
                        });
    }

    getContent = function() {
        AdminAPIService
        .doApiCall({
            "req_name" : "getContent",
            "params" : {}
        })
        .success(
                function(data) {
                    $scope.contents = data.result
                });
    }

    unreadMessagesCount = function() {
        AdminAPIService
        .doApiCall({
            "req_name" : "getUnreadMessagesCount",
            "params" : {}
        })
        .success(
                function(data) {
                    $window.localStorage.setItem("unreadMessagesCount", data);
                });
    }
    unreadMessagesCount();

    getTodayAndYesterdayVisits = function() {
        AdminAPIService
                .doApiCall({
                    "req_name" : "getTodayAndYesterdayVisits",
                    "params" : {}
                })
                .success(
                        function(data) {
                            $scope.labels = [ "Yesterday", "Today"];
                            $scope.data = [data.yesterdaysVisits, data.todaysVisits];

                        });
    }

    getVisitsByCountries = function() {
        AdminAPIService
                .doApiCall({
                    "req_name" : "getVisitsByCountries",
                    "params" : {}
                })
                .success(
                        function(data) {
                            $scope.donutLabels = data.countries;
                            $scope.donutData = data.visits;
                        });
    }

    getModulesDetails = function() {
        AdminAPIService
                .doApiCall({
                    "req_name" : "getModulesDetails",
                    "params" : {}
                })
                .success(
                        function(data) {
                            $scope.movies = data.movies;
                            $scope.cricket = data.cricket;
                            $scope.f1 = data.f1;
                            $scope.fc = data.fantasyCricket;
                        });
    }

    getContent();
    getModulesDetails();
    getTodayAndYesterdayVisits();
    getVisitsByCountries();
    getVisits();

    // reference -- http://jtblin.github.io/angular-chart.js/

    $scope.modalParams = 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.';
    $scope.limitLetters = '470';

    $scope.seeMore = function(content_length) {
        $scope.limitLetters = content_length;
    }

        $scope.open = function(content) {
        var modalInstance = $uibModal.open({
            controller : "ModalInstanceCtrl",
            templateUrl : 'myModalContent.html',
            resolve : {
                content : function() {
                    return content;
                }
            }
        });
    };
    $rootScope.unreadMessagesCount = $window.localStorage.getItem("unreadMessagesCount");
});

var navbarController = function($scope, $rootScope, AuthenticationService, $location, localStorageService) {
    $rootScope.isNavCollapsed = true;
    $scope.isCollapsed = true;
    $scope.isCollapsedHorizontal = true;

    var loggedin = localStorageService.get('globals');
    if (loggedin == undefined || loggedin == null || loggedin == '') {
        $("#sidebar-wrapper").detach();
        $("#wrapper").toggleClass("active");
        $("#menu-toggle").detach();
        $("#navbar > ul").detach();
    }
    /* Menu-toggle */
    $("#menu-toggle").click(function(e) {
        e.preventDefault();
        $("#wrapper").toggleClass("active");
    });

    $scope.logout = function() {
        AuthenticationService.ClearCredentials();
        $location.path('/login');
        location.reload();
    }
}

var editMoviesController = function($scope, APIService, $http, $mdConstant) {

    var getMoviesToEdit = function() {
        APIService
                .doApiCall({
                    "req_name" : "getMoviesToEdit",
                    "params" : {},
                })
                .success(
                        function(data) {
                            if (data != undefined && data != null) {
                                for (i = 0; i < data.movieList.length; i++) {
                                    data.movieList[i].releaseDate = new Date(
                                            data.movieList[i].releaseDate);
                                }
                            }

                            $scope.movieTypes = data.movieTypes.result;
                            $scope.movies = data.movieList.reverse();

                            // Pagination logic for movies.
                            $scope.filteredTodos = []
                            $scope.currentPage = 1
                            $scope.numPerPage = 10
                            $scope.maxSize = 5;

                            $scope.numPages = function() {
                                return Math.ceil($scope.movies.length
                                        / $scope.numPerPage);
                            };
                            $scope
                                    .$watch(
                                            'currentPage + numPerPage',
                                            function() {
                                                var begin = (($scope.currentPage - 1) * $scope.numPerPage), end = begin
                                                        + $scope.numPerPage;
                                                $scope.filteredMovies = $scope.movies
                                                        .slice(begin, end);
                                            });
                        });
    }
    getMoviesToEdit();

    $scope.setMovie = function(movie) {
        APIService.doApiCall({
            "req_name" : "setMovie",
            "params" : {
                "movieName" : movie.movieName,
                "releaseDate" : movie.releaseDate,
                "cast" : movie.cast,
                "trailer" : movie.trailer,
                "movieType" : movie.movieType
            }
        }).success(function(data) {
            if (data != null || data != "") {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Created Successfully.'
                } ];
            } else {
                $scope.alerts = [ {
                    type : 'warning',
                    msg : 'Oops! There seems to be some error.'
                } ];
            }
            getMoviesToEdit();
        });
    }

    $scope.removeMovie = function(movie) {
        if (movie.isDelete) {
            APIService.doApiCall({
                "req_name" : "removeMovie",
                "params" : {
                    "movieId" : movie.movieId
                }
            }).success(function(data) {
                if (data != null || data != "") {
                    $scope.alerts = [ {
                        type : 'success',
                        msg : 'Nice! Record Removed Successfully.'
                    } ];
                } else {
                    $scope.alerts = [ {
                        type : 'warning',
                        msg : 'Oops! There seems to be some error.'
                    } ];
                }
                getMoviesToEdit();
            });
        } else {
            $scope.alerts = [ {
                type : 'danger',
                msg : 'No Row Selected'
            } ];
        }
    }

    $scope.closeAlert = function() {
        $scope.alerts = "";
    }

    $scope.seperatorKeys = [ $mdConstant.KEY_CODE.ENTER,
            $mdConstant.KEY_CODE.COMMA ];
    $scope.addMultipleChips = function(chip, model, index) {
        var seperatedString = angular.copy(chip);
        seperatedString = seperatedString.toString();
        var chipsArray = seperatedString.split(', ');
        angular.forEach(chipsArray, function(chipToAdd) {
            $scope[model][index - 1].cast.push(chipToAdd);
        });
        return null;
    };

    $scope.updateMovie = function(movie) {
        APIService.doApiCall({
            "req_name" : "updateMovie",
            "params" : {
                "movieName" : movie.movieName,
                "releaseDate" : movie.releaseDate,
                "cast" : movie.cast,
                "trailer" : movie.trailer,
                "movieId" : movie.movieId,
                "movieType" : movie.movieType
            }
        }).success(function(data) {
            if (data != null || data != "") {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Updated Successfully.'
                } ];
            } else {
                $scope.alerts = [ {
                    type : 'warning',
                    msg : 'Oops! There seems to be some error.'
                } ];
            }
            getMoviesToEdit();
        });
    }

    $scope.openDatePicker = function($event, f1) {
        $event.preventDefault();
        $event.stopPropagation();
        f1.opened = true;
    };

    $scope.dateOptions = {
        formatYear : 'yy',
        startingDay : 1, // shows which day of the week to pre-select on
                            // opening the datepicker
    };

    $scope.format = 'dd/MMMM/yyyy';

    // Time picker
    $scope.hstep = 1;
    $scope.mstep = 15;
    $scope.ismeridian = true;

    $scope.addNew = function() {
        $scope.filteredMovies.push({
            addBtn : true,
            cast : []
        });
    };
}

var editCricketController = function($scope, APIService, $http, $uibModal,
        $stateParams) {

    addNewSeries = false;
    $scope.addNewSeries = addNewSeries;

    getCricketCountries = function() {
        APIService.doApiCall({
            "req_name" : "getCricketCountries",
            "params" : {}
        }).success(function(data) {
            $scope.teams = data;
        });
    }

    getCricketSeries = function() {
        APIService.doApiCall({
            "req_name" : "getAllRawCricketLeagues",
            "params" : {}
        }).success(function(data) {
            $scope.seriesList = data.result;
        });
    }
    getCricketSeries();

    $scope.addCricketSeries = function() {
        addNewSeries = !addNewSeries;
        $scope.addNewSeries = addNewSeries;
    }

    $scope.deleteCricketLeague = false;

    $scope.addRemoveCricketLeague = function(cricketLeague, isDelete) {
        if (isDelete) {
            APIService.doApiCall({
                "req_name" : "addRemoveSportsLeague",
                "params" : {
                    "sports_league_id" : cricketLeague.series_id,
                    "sports_type_id" : "CRICKET"
                }
            }).success(function(data) {
                getCricketSeries();
                $scope.deleteCricketLeague = false;
            });
        } else {
            APIService.doApiCall({
                "req_name" : "addRemoveSportsLeague",
                "params" : {
                    "series_name" : cricketLeague.series,
                    "series_location" : cricketLeague.series_location,
                    "sports_type_id" : "CRICKET",
                    "series_from_date" : cricketLeague.series_from_date,
                    "series_to_date" : cricketLeague.series_to_date,
                    "country_with" : cricketLeague.country_with
                }
            }).success(function(data) {
                getCricketSeries();
                $scope.cricketLeague = "";
            });
        }
    }

    updateCricketSeries = function() {
        APIService.doApiCall({
            "req_name" : "updateCricketSeries",
            "params" : {}
        }).success(function(data) {
            $scope.seriesList = data.result;
        });
    }

    removeCricketSeries = function() {
        APIService.doApiCall({
            "req_name" : "removeCricketSeries",
            "params" : {}
        }).success(function(data) {
            $scope.seriesList = data.result;
        });
    }

    getCricketMatchTypes = function() {
        APIService.doApiCall({
            "req_name" : "getCricketMatchTypes",
            "params" : {}
        }).success(function(data) {
            $scope.matchTypes = data.result;
        });
    }

    getCricketMatchTypes();
    getCricketCountries();

    getIntlCricket = function() {
        APIService
                .doApiCall({
                    "req_name" : "getIntlCricket",
                    "params" : {}
                })
                .success(
                        function(data) {
                            for (i = 0; i < data.result.length; i++) {
                                data.result[i].match_from_date = new Date(
                                        data.result[i].match_from_date);
                                data.result[i].match_to_date = new Date(
                                        data.result[i].match_to_date);
                                data.result[i].time = new Date(
                                        data.result[i].time);
                            }
                            $scope.cricketList = data.result;

                            // Pagination Code for Cricket
                            $scope.filteredTodos = []
                            $scope.currentPage = 1
                            $scope.numPerPage = 10
                            $scope.maxSize = 5;

                            $scope.numPages = function() {
                                return Math.ceil($scope.cricketList.length
                                        / $scope.numPerPage);
                            };
                            $scope
                                    .$watch(
                                            'currentPage + numPerPage',
                                            function() {
                                                var begin = (($scope.currentPage - 1) * $scope.numPerPage), end = begin
                                                        + $scope.numPerPage;
                                                $scope.filteredCricketList = $scope.cricketList
                                                        .slice(begin, end);
                                            });
                        });
    }
    getIntlCricket();

    $scope.createCricket = function(cricket) {
        APIService.doApiCall({
            "req_name" : "setCricket",
            "params" : {
                "teamOneId" : cricket.team_one_geoId,
                "teamTwoId" : cricket.team_two_geoId,
                "stadium" : cricket.stadium,
                "city" : cricket.city,
                "matchNumber" : cricket.match_number,
                "country" : cricket.country_geoId,
                "matchType" : cricket.sports_child_type_id,
                "fromDate" : cricket.match_from_date,
                "toDate" : cricket.match_to_date,
                "time" : cricket.time,
                "sports_league" : cricket.series_id
            }
        }).success(function(data) {
            if (data != null || data != "") {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Created Successfully.'
                } ];
            } else {
                $scope.alerts = [ {
                    type : 'warning',
                    msg : 'Oops! There seems to be some error.'
                } ];
            }
            getIntlCricket();
        });
    }

    $scope.updateCricket = function(cricket, $scope) {
        if ("Invalid Date" == cricket.match_to_date) {
            cricket.match_to_date = "N/A";
        }
        APIService.doApiCall({
            "req_name" : "updateCricket",
            "params" : {
                "cricketId" : cricket.cricket_id,
                "teamOneId" : cricket.team_one_geoId,
                "teamTwoId" : cricket.team_two_geoId,
                "stadium" : cricket.stadium,
                "city" : cricket.city,
                "matchNumber" : cricket.match_number,
                "country" : cricket.country_geoId,
                "matchType" : cricket.sports_child_type_id,
                "fromDate" : cricket.match_from_date,
                "toDate" : cricket.match_to_date,
                "time" : cricket.time,
                "sports_league" : cricket.series_id
            }
        }).success(function(data) {
            if (data != null || data != "") {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Updated Successfully.'
                } ];
            } else {
                $scope.alerts = [ {
                    type : 'warning',
                    msg : 'Oops! There seems to be some error.'
                } ];
            }
            getIntlCricket();
        });
    }

    $scope.removeCricket = function(cricket) {
        if (cricket.isDelete) {
            APIService.doApiCall({
                "req_name" : "removeCricket",
                "params" : {
                    "cricketId" : cricket.cricket_id
                }
            }).success(function(data) {
                if (data != null || data != "") {
                    $scope.alerts = [ {
                        type : 'success',
                        msg : 'Nice! Record Deleted Successfully.'
                    } ];
                } else {
                    $scope.alerts = [ {
                        type : 'warning',
                        msg : 'Oops! There seems to be some error.'
                    } ];
                }
                getIntlCricket();
            });
        } else {
            $scope.alerts = [ {
                type : 'danger',
                msg : 'No Row Selected'
            } ];
        }
    }

    $scope.addNew = function() {
        $scope.cricketList.push({
            addBtn : true
        });
    };
}

var editF1Controller = function($scope, $http, APIService, $filter) {

    $scope.myVal = 'submit';
    $scope.coll = {};

    var getFormula1ToEdit = function() {
        APIService
                .doApiCall({
                    "req_name" : "getFormula1ToEdit",
                    "params" : {},
                })
                .success(
                        function(data) {
                            for (i = 0; i < data.formula1List.length; i++) {
                                data.formula1List[i].result.date = new Date(
                                        data.formula1List[i].result.date);
                                data.formula1List[i].result.image = "";
                            }
                            $scope.formula1 = data.formula1List;
                            $scope.raceTypes = data.raceTypes;

                            // Code for pagination on Create Formula 1 Screen
                            $scope.filteredTodos = []
                            $scope.currentPage = 1
                            $scope.numPerPage = 10
                            $scope.maxSize = 5;

                            $scope.numPages = function() {
                                return Math.ceil($scope.formula1.length
                                        / $scope.numPerPage);
                            };
                            $scope
                                    .$watch(
                                            'currentPage + numPerPage',
                                            function() {
                                                var begin = (($scope.currentPage - 1) * $scope.numPerPage), end = begin
                                                        + $scope.numPerPage;
                                                $scope.filteredformula1 = $scope.formula1
                                                        .slice(begin, end);
                                            });
                        });
    }

    getFormula1ToEdit();

    $scope.practice = function(f1) {
        APIService.doApiCall({
            "req_name" : "updateF1Practice",
            "params" : {
                "time" : f1.time,
                "date" : f1.date,
                "raceType" : f1.type,
                "formulaOneId" : f1.id,
                "formulaOnePracticeId" : f1.practiceId,
                "name" : f1.name,
                "country" : f1.country,
                "city" : f1.city,
                "imagePath" : f1.imagePath,
                "circuitGuide" : f1.circuitGuide
            }
        }).success(function(data) {
            if (data != null || data != "") {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Updated Successfully.'
                } ];
            } else {
                $scope.alerts = [ {
                    type : 'warning',
                    msg : 'Oops! There seems to be some error.'
                } ];
            }
            $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
            };
        });
    }

    $scope.createF1 = function(f1) {
        APIService.doApiCall({
            "req_name" : "setF1Schedule",
            "params" : {
                "time" : f1.time,
                "date" : f1.date,
                "raceType" : f1.type,
                "name" : f1.name,
                "country" : f1.country,
                "city" : f1.city,
                "formula1Id" : f1.id,
                "imagePath" : f1.imagePath,
                "circuitGuide" : f1.circuitGuide
            }
        }).success(function(data) {
            if (data != null || data != "") {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Updated Successfully.'
                } ];
            } else {
                $scope.alerts = [ {
                    type : 'warning',
                    msg : 'Oops! There seems to be some error.'
                } ];
            }
            $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
            };
            $scope.formula1.addBtn = false;
            getFormula1ToEdit();
        });
    }

    $scope.removeF1 = function(f1) {
        if (f1.isDelete) {
            APIService.doApiCall({
                "req_name" : "removeF1Schedule",
                "params" : {
                    "raceType" : f1.type,
                    "formula1Id" : f1.id,
                    "formula1PracticeId" : f1.practiceId
                }
            }).success(function(data) {
                if (data != null || data != "") {
                    $scope.alerts = [ {
                        type : 'success',
                        msg : 'Nice! Record Updated Successfully.'
                    } ];
                } else {
                    $scope.alerts = [ {
                        type : 'warning',
                        msg : 'Oops! There seems to be some error.'
                    } ];
                }
                $scope.closeAlert = function(index) {
                    $scope.alerts.splice(index, 1);
                };
                var newDataList = [];
                angular.forEach($scope.formula1, function(v) {
                    if (!v.result.isDelete) {
                        newDataList.push(v);
                    }
                });
                $scope.formula1 = newDataList;

                getFormula1ToEdit();
            });
        } else {
            $scope.alerts = [ {
                type : 'danger',
                msg : 'No Row Selected'
            } ];
        }
    }

    // Disable weekend selection
    $scope.openDatePicker = function($event, f1) {
        $event.preventDefault();
        $event.stopPropagation();
        f1.opened = true;
    };

    $scope.dateOptions = {
        formatYear : 'yy',
        startingDay : 1, //shows which day of the week to pre-select on opening the datepicker
        maxDate : new Date(2018, 5, 22),
        minDate : new Date(2017, 00, 01),
    };

    $scope.format = 'dd/MMMM/yyyy';

    //Time picker
    $scope.hstep = 1;
    $scope.mstep = 15;
    $scope.ismeridian = true;
    $scope.addNew = function() {
        $scope.formula1.push({
            addBtn : true
        });
    };
}

var editFantasyCricketController = function($scope, APIService, $http,
        $uibModal, $stateParams) {

    getCricketCountries = function() {
        APIService.doApiCall({
            "req_name" : "getCricketCountries",
            "params" : {}
        }).success(function(data) {
            $scope.countries = data;
        });
    }

    $scope.createFantasyRecord = function(playerDetails) {
        APIService.doApiCall({
            "req_name" : "setFantasyCricketRecord",
            "params" : {
                "firstName" : playerDetails.firstName,
                "lastName" : playerDetails.lastName,
                "battingRating" : playerDetails.rating,
                "bowlingRating" : playerDetails.bowlingRating,
                "role" : playerDetails.role,
                "countryGeoId" : playerDetails.countryGeoId,
                "battingPosition" : playerDetails.battingPosition
            }
        }).success(function(data) {
            if ("success" == data) {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Created Successfully.'
                } ];
                $scope.getFantasyRecords();
            }
        });
    }

    $scope.getFantasyRecords = function() {
        APIService
                .doApiCall({
                    "req_name" : "getFantasyCricketPlayers",
                    "params" : {}
                })
                .success(
                        function(data) {
                            $scope.fantasyCricket = data;

                            //Pagination Logic for fantasy Cricket.
                            $scope.filteredTodos = []
                            $scope.currentPage = 1
                            $scope.numPerPage = 10
                            $scope.maxSize = 5;

                            $scope.numPages = function() {
                                return Math.ceil($scope.fantasyCricket.length
                                        / $scope.numPerPage);
                            };
                            $scope
                                    .$watch(
                                            'currentPage + numPerPage',
                                            function() {
                                                var begin = (($scope.currentPage - 1) * $scope.numPerPage), end = begin
                                                        + $scope.numPerPage;
                                                $scope.filteredFantasyCricket = $scope.fantasyCricket
                                                        .slice(begin, end);
                                            });
                        });
    }

    $scope.updateFantasyCricket = function(playerDetails) {
        APIService.doApiCall({
            "req_name" : "updateFantasyCricket",
            "params" : {
                "fantasyCricketId" : playerDetails.fantasy_cricket_id,
                "firstName" : playerDetails.firstName,
                "lastName" : playerDetails.lastName,
                "battingRating" : playerDetails.rating,
                "bowlingRating" : playerDetails.bowlingRating,
                "role" : playerDetails.role,
                "countryGeoId" : playerDetails.countryGeoId,
                "battingPosition" : playerDetails.battingPosition
            }
        }).success(function(data) {
            if ("success" == data) {
                $scope.alerts = [ {
                    type : 'success',
                    msg : 'Nice! Record Updated Successfully.'
                } ];
                $scope.getFantasyRecords();
            }
        });
    }

    $scope.removeFantasyRecords = function(fantasyCricketId) {
        APIService.doApiCall({
            "req_name" : "removeFantasyCricketRecord",
            "params" : {
                "fantasyCricketId" : fantasyCricketId
            }
        }).success(function(data) {
            $scope.getFantasyRecords();
        });
    }

    getCricketCountries();
    $scope.getFantasyRecords();

    $scope.fantasyCricket = [];

    $scope.addNew = function() {
        $scope.fantasyCricket.push({
            addBtn : true
        });
    };
}

/* -- Custom Functions -- */
function getPagination(scopeVariable, $scope) {
    //Pagination Logic for fantasy Cricket.
    $scope.filteredTodos = []
    $scope.currentPage = 1
    $scope.numPerPage = 10
    $scope.maxSize = 5;

    var filteredScopeVariable = "filtered" + capitalizeFirstLetter(scopeVariable);

    $scope.numPages = function() {
        return Math.ceil($scope[scopeVariable].length
                / $scope.numPerPage);
    };
    $scope.$watch('currentPage + numPerPage',
        function() {
            var begin = (($scope.currentPage - 1) * $scope.numPerPage), end = begin
                    + $scope.numPerPage;
            $scope[filteredScopeVariable] = $scope[scopeVariable].slice(begin, end);
        });
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

/* ------- Controller Entries ------- */
myApp.controller("navbarController", navbarController);
myApp.controller("editMoviesController", editMoviesController);
myApp.controller("editCricketController", editCricketController);
myApp.controller("editF1Controller", editF1Controller);
myApp.controller("editFantasyCricketController", editFantasyCricketController);
myApp.controller("userMessagesController", userMessagesController);
myApp.controller("loginController", loginController);

/* ----- Routing ----- */
myApp.config(function($stateProvider, $urlRouterProvider, $locationProvider, localStorageServiceProvider) {
    localStorageServiceProvider.setPrefix('nextrr');
    $stateProvider.state("home", {
        url : "/",
        templateUrl : "dashboard.html",
        controller : "dashboardController"
    }).state("movies", {
        url : "/movies",
        templateUrl : "moviesInsert.html",
        controller : "editMoviesController"
    }).state("cricket", {
        url : "/cricket",
        templateUrl : "cricketInsert.html",
        controller : "editCricketController"
    }).state("formula-one", {
        url : "/f1",
        templateUrl : "F1Insert.html",
        controller : "editF1Controller"
    }).state("fantasy-cricket", {
        url : "/fantasy-cricket",
        templateUrl : "fantasyCricketInsert.html",
        controller : "editFantasyCricketController"
    }).state("user-message", {
        url : "/user-messages",
        templateUrl : "userMessages.html",
        controller : "userMessagesController"
    }).state("login", {
        url : "/login",
        templateUrl : "login.html",
        controller : "loginController"
    }).state("otherwise", {
        url : "/otherwise",
        templateUrl : "login.html",
        controller : "loginController"
    });
}).run(['$rootScope', '$location', '$http', 'localStorageService', 
    function ($rootScope, $location, $http, localStorageService) {
    // keep user logged in after page refresh
    $rootScope.globals = localStorageService.get('globals') || {};
    if ($rootScope.globals.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
    }

    $rootScope.$on('$locationChangeStart', function (event, next, current) {
        // redirect to login page if not logged in
        if ($location.path() !== '/login' && !$rootScope.globals.currentUser) {
            $location.path('/login');
        }
    });
}]);