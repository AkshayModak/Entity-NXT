
var myApp = angular.module("myModule", ["ui.router",'ngAnimate', 'ngSanitize', "ui.bootstrap", "ngMaterial", "ngMessages", "infinite-scroll"]);

/*
 *
 * pageTitle: Title which will be displayed on tabs.
 * APIService: Custom service to hit Http Requests. Using jersey for rest calls.
 * 
 */

/* ======= Utility Functions =======*/
function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min; //return a random number in between certain range
}

function format_time(date_obj) {
    // formats a javascript Date object into a 12h AM/PM time string
    var hour = date_obj.getHours();
    var minute = date_obj.getMinutes();
    var amPM = (hour > 11) ? "pm" : "am";
    if (hour > 12) {
        hour -= 12;
    } else if (hour == 0) {
        hour = "12";
    }
    if (minute < 10) {
        minute = "0" + minute;
    }
    return hour + ":" + minute + amPM;
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

//Generic pagination logic
function getPagination(scopeVariable, $scope, begin) {
    $scope.filteredTodos = []
    $scope.maxSize = begin + 10;
    var listLength = $scope[scopeVariable].length;
    var filteredScopeVariable = "filtered" + capitalizeFirstLetter(scopeVariable);

    $scope[filteredScopeVariable] = $scope[scopeVariable].slice(begin, $scope.maxSize);
    angular.element(document).on('scroll', function() {
        // do your things like logging the Y-axis
        if ((window.innerHeight + window.scrollY) >= jQuery(this).innerHeight()) {
            // you're at the bottom of the page
            var end = $scope[filteredScopeVariable].length + $scope.maxSize;
            $scope[filteredScopeVariable] = $scope[scopeVariable].slice(begin, end);
        }
    });
}

/* ======== Controller Definitions =======*/
var homeController = function($scope, $rootScope, APIService) {
    $rootScope.home = true;
    $rootScope.pageTitle = "Home | Nextrr";

    getContent = function() {
        APIService
        .doApiCall({
            "req_name" : "getContentByCondition",
            "params" : {"screen": "nextrr_home"}
        }).success(
                function(data) {
                    content = data.result;
                    $scope.movieContent = content[0];
                    $scope.cricketContent = content[1];
                    $scope.f1Content = content[2];
                    $scope.fantasyCricketContent = content[3];
                });
    }
    getContent();
}

var navbarController = function ($scope, $rootScope) {
    $rootScope.isNavCollapsed = true;
    $scope.isCollapsed = true;
    $scope.isCollapsedHorizontal = true;
}

var fantasyCricketController = function($scope, $rootScope, APIService, ModalService, $http, $uibModal, $stateParams) {
    getFantasyCricketPlayers = function() {
        $rootScope.showLoader = true;
        APIService.doApiCall({
            "req_name": "getFantasyCricketPlayers",
            "params": {}
        }).success(function(data) {
            for (i = 0; i < data.size; i++) {
                data[i].pushed = "";
            }
            $scope.players = data;
            $rootScope.showLoader = false;
        });
    }

    $scope.userEleven = [];
    $scope.userEleven.teamName = "Hellraiser XI"; //Default Player team name

    $scope.rating = 0;
    $scope.ratingExceedAlert = false;
    $scope.disableBatmen = false;
    $scope.disableBowlers = false;
    $scope.disableWicketkeepers = false;
    $scope.disableAllRounders = false;

    //Count of player categories
    var totalBatsmen = 0;
    var totalWicketkeepers = 0;
    var totalBowlers = 0;
    var totalAllRounders = 0;

    $scope.addToEleven = function(item) {
        if ("bowler" == item.role || "spinner" == item.role) {
            $scope.rating = $scope.rating + parseInt(item.bowlingRating);
            totalBowlers = totalBowlers + 1;
        } else if ("batsman" == item.role) {
            $scope.rating = $scope.rating + parseInt(item.rating);
            totalBatsmen = totalBatsmen + 1;
        } else if ("wicketkeeper" == item.role) {
            $scope.rating = $scope.rating + parseInt(item.rating);
            totalWicketkeepers = totalWicketkeepers + 1;
        } else if ("all-rounder-spinner" == item.role || "all-rounder-fast" == item.role) {
            $scope.rating = $scope.rating + parseInt(item.rating);
            totalAllRounders = totalAllRounders + 1;
        }

        if (totalBatsmen >= 5) {            //Can Only select 5 batsman at a time
             $scope.disableBatsmen = true;
        }
        if (totalBowlers >= 3) {            //Can't select more than 3 bowlers at a time
            $scope.disableBowlers = true;
        }
        if (totalWicketkeepers >= 1) {      //Can't select more than 1 wicket-keeper
            $scope.disableWicketkeepers = true;
        }
        if (totalAllRounders >= 2) {        //Can't select more than 1 All-Rounders
            $scope.disableAllRounders = true;
        }
        
        if ($scope.rating <= 100) {         //Disable add player button if rating exceeds 100 mark
            item.pushed = true;
            $scope.userEleven.push(item);

            if ($scope.userEleven.length == 11) {
                $scope.showPlayBtn = true;
            }
            $scope.ratingExceedAlert = false;
        } else {
            $scope.ratingExceedAlert = true;
        }
    }

    $scope.removeFromEleven = function(item) {
        item.pushed = false;
        $scope.showPlayBtn = false;
        $scope.ratingExceedAlert = false;
        var index = $scope.userEleven.indexOf(item);
        $scope.userEleven.splice(index, 1);

        if ("bowler" == item.role || "spinner" == item.role) {
            $scope.rating = $scope.rating - parseInt(item.bowlingRating);
            totalBowlers = totalBowlers - 1;
        } else if ("batsman" == item.role) {
            $scope.rating = $scope.rating - parseInt(item.rating);
            totalBatsmen = totalBatsmen - 1;
        } else if ("wicketkeeper" == item.role) {
            $scope.rating = $scope.rating - parseInt(item.rating);
            totalWicketkeepers = totalWicketkeepers - 1;
        } else if ("all-rounder-spinner" == item.role || "all-rounder-fast" == item.role) {
            $scope.rating = $scope.rating - parseInt(item.rating);
            totalAllRounders = totalAllRounders - 1;
        }

        /*Enable disabled add button if player limit of certain category 
          remains under rating limit. ie. 100 */
        if (totalBatsmen < 5) {
             $scope.disableBatsmen = false;
        }
        if (totalBowlers < 3) {
            $scope.disableBowlers = false;
        }
        if (totalWicketkeepers < 1) {
            $scope.disableWicketkeepers = false;
        }
        if (totalAllRounders < 2) {
            $scope.disableAllRounders = false;
        }
    }

    $scope.showScoreboard = false;
    $scope.playCricket = function(userEleven, computerPlayers, playingAgainst, tossPreference) {
        if (computerPlayers == undefined || computerPlayers.length < 11) {
            //Display error message if user submits before selecting opposition team.
            $scope.errorMessage = "Please select country to play against.";
            return;
        }
        if (userEleven.length == 11) {
            $scope.showSpinner = true;
            APIService.doApiCall({
                "req_name": "getFantasyCricketResult",
                "params": {"userEleven" : userEleven,"computerPlayers" : computerPlayers, "tossPreference" : tossPreference}
            }).success(function(data) {
                $scope.tossPreference = data[0];
                if ("user" == data) {
                    $scope.tossPreference = true;
                    $scope.tossMessage = userEleven.teamName + " Won the toss." //Display when user wins the toss.
                    $scope.wonBy = "user";
                } else if ("computer-bat" == data) {
                    $scope.tossPreference = true;
                    $scope.tossMessage = playingAgainst + " has won the toss and elected to bat first"; //Display when computer elects to bat first.
                    $scope.wonBy = "computer";
                } else if ("computer-bowl" == data) {
                    $scope.tossPreference = true;
                    $scope.tossMessage = playingAgainst + " has won the toss and elected to bowl first"; //Display when computer elects to bowl first.
                    $scope.wonBy = "computer";
                }
                $scope.team1 = data[1];
                $scope.team1Fow = data[2];
                $scope.team1BowlerDetails = data[3];
                $scope.team1Score = data[4];

                $scope.team2 = data[5];
                $scope.team2Fow = data[6];
                $scope.team2BowlerDetails = data[7];
                $scope.team2Score = data[8];
                
                $scope.showScoreboard = true;
            });
            $scope.showSpinner = false;
        } else {
            playersRequired = 11 - userEleven.length;
            $scope.errorMessage = "Need " + playersRequired +" more player(s) to play a match."; //Display if user tries to play with less than 11 players
        }
    }

    getCricketCountries = function() {
        APIService.doApiCall({
            "req_name": "getCountryAssoc",
            "params": {"sports_type_id": "CRICKET", "format": "GSON"}
        }).success(function(data) {
            $scope.teams = data;
        });
    }

    $scope.hideError = function() {
        $scope.errorMessage = false;
    }

    $scope.setPlayAgainst = function(_team, _teamName) {
        APIService.doApiCall({
            "req_name": "setPlayAgainst",
            "params": {"playAgainst": _team}
        }).success(function(data) {
            $scope.playingAgainst = _teamName;
            $scope.computerPlayers = data;
        });
    }

    $rootScope.pageTitle = "Fantasy Cricket | Nextrr";

    getCricketCountries();
    getFantasyCricketPlayers();
}

var cricketController = function($scope, APIService, ModalService, $http, $uibModal, $stateParams, $rootScope) {
    $scope.expanded = false;

    //filter cricket list based on passed conditions. Used to filter based on Country and to clear selected country list.
    $scope.filterCricket = function(string) {
        $scope.filterString = "";
        if (string == '') {
            $scope.dropdown_index = -1;
            getPagination('cricketList', $scope, $scope.finishedMatches);
        } else {
            $scope.filteredCricketList = $scope.cricketList;
        }
        $scope.filterString = string;
        $scope.selectedTeam = string;
    }

    //Toggle to display all Cricket matches. ie. Completed and Upcoming
    $scope.showAllCricket = function() {
        if ($scope.released) {
            var today = new Date();
            today.setHours(0,0,0,0);
            cricketList = $scope.cricketList;
            for (i=0; i < cricketList.length; i++) {
                cricketList[i].displayCricket = "display-block";
            }
            $scope.cricketList = cricketList;
            getCricketLeagues(false);
        } else {
            var today = new Date();
            today.setHours(0,0,0,0);
            cricketList = $scope.cricketList;
            for (i=0; i < cricketList.length; i++) {
                var matchDate = new Date(cricketList[i].match_date);
                if (matchDate < today) {
                    cricketList[i].displayCricket = "display-none";
                } else {
                    cricketList[i].displayCricket = "display-block";
                }
            }
            $scope.cricketList = cricketList;
            data = $scope.cricketLeagues;
            for (i = 0; i < data.length; i++) {
                if (data[i].sports_leagues != undefined) {
                    for (j = 0; j < data[i].sports_leagues.length; j++) {
                        leagueDate = new Date(data[i].sports_leagues[j].to_date);
                        if (leagueDate < today) {
                            data[i].sports_leagues.splice(j, 1);
                        }
                    }
                }
                if (!(data[i].sports_leagues).length > 0) {
                    data.splice(i, 1);
                }
            }
        }
        $scope.filteredCricketList = cricketList;
    }
    $scope.cricketList = [];

    getIntlCricketToDisplay = function() {
        $rootScope.showLoader = true;
        APIService.doApiCall({
            "req_name": "getIntlCricketToDisplay",
            "params": {}
        }).success(function(data) {
            cricketList = data.result;
            var colors = ['#1a237e', '#880e4f', '#4a148c', '#004d40', '#6d4c41', '#455a64']; //Color list for cricket-bar. Chosen randomly
            var today = new Date();
            today.setHours(0,0,0,0);
            $scope.finishedMatches = 0;
            for (i=0; i < cricketList.length; i++) {
                var randomColor = getRandomInt(0, 4);
                cricketList[i].barColor = colors[randomColor];
                matchDate = new Date(cricketList[i].match_date);
                if (matchDate < today) {
                    cricketList[i].displayCricket = "display-none";
                    $scope.finishedMatches = $scope.finishedMatches + 1;
                } else {
                    cricketList[i].displayCricket = "display-block";
                }
            }
            $scope.cricketList = cricketList;
            getPagination('cricketList', $scope, $scope.finishedMatches);
            $rootScope.showLoader = false;
        });
    }
    getIntlCricketToDisplay();

    getCricketLeagues = function(_recent) {
        APIService.doApiCall({
            "req_name": "getCricketLeagues",
            "params": {}
        }).success(function(data) {
            if (_recent) {
                today = new Date();
                today.setHours(0,0,0,0);
                for (i = 0; i < data.length; i++) {
                    if (data[i].sports_leagues != undefined) {
                        for (j = 0; j < data[i].sports_leagues.length; j++) {
                            leagueDate = new Date(data[i].sports_leagues[j].to_date);
                            if (leagueDate < today) {
                                data[i].sports_leagues.splice(j, 1);
                            }
                        }
                    }
                    if ((data[i].sports_leagues).length == 0) {
                        data.splice(i, 1);
                    }
                }
            }
            $scope.cricketLeagues = data;
        });
    }

    $scope.expandDropdown = function(expanded, index) {
        if (index == $scope.dropdown_index) {
            $scope.expanded = !expanded;
            $scope.dropdown_index = -1;
        } else {
            $scope.expanded = !expanded;
            $scope.dropdown_index = index;
        }
    }

    getCricketCountries = function() {
        APIService.doApiCall({
            "req_name": "getCountryAssoc",
            "params": {"sports_type_id": "CRICKET", "format": "GSON"}
        }).success(function(data) {
            $scope.teams = data;
        });
    }

    getCricketCountries();
    getCricketLeagues(true);

    $rootScope.pageTitle = "Cricket | Nextrr";
}

var moviesController = function($scope, APIService, ModalService, $http, $uibModal, $stateParams, $rootScope) {
    var movieType = $stateParams.movieType;
    if ("hollywood" === movieType) {
        $scope.selectedType = 0;
    } else {
        $scope.selectedType = 1;
    }

    var getMovies = function() {
        $rootScope.showLoader = true;
        APIService.doApiCall({
            "req_name": "getMovies",
            "params": {movieType: movieType},
        }).success(function(data) {
            var colors = ['#1a237e', '#880e4f', '#4a148c', '#004d40', '#6d4c41', '#455a64']; //Movie-bar colors. Chosen randomly
            data.sort(function(a, b) {
                /* Turn your strings into dates, and then subtract them
                 to get a value that is either negative, positive, or zero. */
                return new Date(a.releaseDate) - new Date(b.releaseDate);
            });
            var releasedMovies = 0;
            for (i = 0; i < data.length; i++) {
                var randomColor = getRandomInt(0, 4);
                data[i].movieColor = colors[randomColor];
                var today = new Date();
                today.setHours(0,0,0,0);
                var releaseDate = new Date(data[i].releaseDate);
                if (releaseDate < today) {
                    data[i].displayMovie = "none";
                    releasedMovies = releasedMovies + 1;
                }
            }
            $scope.movies = data;
            getPagination('movies', $scope, releasedMovies);
            $rootScope.showLoader = false;
        });
    }

    if ("hollywood" === movieType) {
        getMovies();
    } else {
        getMovies();
    }

    /* Open trailer modal with passed movie
     * reference -- http://jsfiddle.net/8s9ss/4/ 
     *  */
    $scope.open = function (trailerLink) {
        var modalInstance = $uibModal.open({
          controller: function($scope, $sce) {
              $scope.trustSrc = function(trailer) {
                  return $sce.trustAsResourceUrl(trailer);
              }
              $scope.trailer = trailerLink;
          },
          templateUrl: 'myModalContent.html'
        });
    };

    //Show all movies. ie. Released and Upcoming
    $scope.showAllMovies = function(movies) {
        var today = new Date();
        today.setHours(0, 0, 0, 0);
        if ($scope.released) {
            for (i = 0; i < movies.length; i++) {
                movies[i].displayMovie = "block";
            }
        } else {
            for (i = 0; i < movies.length; i++) {
                var releaseDate = new Date(movies[i].releaseDate);
                if (releaseDate < today) {
                    movies[i].displayMovie = "none";
                }
            }
        }
        $scope.filteredMovies = movies;
    }

    $rootScope.pageTitle = "Movies | Nextrr";
};

var formula1Controller = function($scope, APIService, $http, $sce, $rootScope) {

    $rootScope.showLoader = true;
    APIService.doApiCall({
        "req_name": "getFormula1Schedule",
        "params": {},
    }).success(function(data) {
    	for (i = 0; i < data.length; i++) {
            var monthNames = [ "January", "February", "March",
                    "April", "May", "June", "July", "August",
                    "September", "October", "November",
                    "December" ];
            data[i].expanded = false;
            var mainRaceDate = new Date(data[i].mainRace.date);
            if (data[i].FIRSTPRACTICE != null || data[i].FIRSTPRACTICE != undefined) {
                var firstPracticeDate = new Date(data[i].FIRSTPRACTICE.date);
                data[i].FIRSTPRACTICE.date = firstPracticeDate.getDate() + " " + monthNames[firstPracticeDate.getMonth()];
            }
            var raceDate = mainRaceDate.getDate();
            var currentDate = new Date().getDate();
            data[i].sortTime = mainRaceDate.getTime();
            data[i].mainRace.date = raceDate + " " + monthNames[mainRaceDate.getMonth()];
            if (mainRaceDate.getTime() < new Date().getTime()) {
                data[i].isFinished = true;
                if (data[i + 1] != undefined) {
                    $scope.nextEvent = data[i + 1];
                    $scope.nextEvent.mainRace.circuitGuide = "https://www.youtube.com/embed/" + data[i + 1].mainRace.circuitGuide;
                    $scope.trustCircuitGuide = function(circuitGuide) {
                        return $sce.trustAsResourceUrl(circuitGuide);
                    }
                }
            }
        }
        $scope.formula1 = data;
        $rootScope.showLoader = false;
    });
    $rootScope.pageTitle = "Formula 1 Schedule | Nextrr";
}

var contactUsController = function($scope, APIService, $rootScope) {
	$scope.setMessage=function(contact){
        console.log(contact);
        if (contact !== null && contact !== '' && contact !== undefined) {
            APIService.doApiCall({
                "req_name": "setMessage",
                "params": {"email":contact.email,"message":contact.message}
            }).success(function(data) {
                console.log(data);
                if (data != null || data != "") {
                    //Messages to display after user submits contact Us.
                    if ('success' === data) {
                        $scope.alerts = [{ type: 'success', msg: 'Thank You for contacting Us. We will try to revert you back as soon as possible.' }];
                    } else {
                        $scope.alerts = [{ type: 'danger', msg: 'Email Address or Message missing.' }];
                    }
                }
                $scope.closeAlert = function(index) {
                    $scope.alerts.splice(index, 1);
                };
            });
            $scope.contact = [];
        }
    }
    $rootScope.pageTitle = "Contact Us | Nextrr";
}

myApp.controller("formula1Controller", formula1Controller);
myApp.controller("moviesController", moviesController);
myApp.controller("cricketController", cricketController);
myApp.controller("fantasyCricketController", fantasyCricketController);
myApp.controller("homeController", homeController);
myApp.controller("contactUsController", contactUsController);
myApp.controller('navbarController', navbarController);

/* ======== Routes Configurations ========*/
myApp.config(function($stateProvider, $urlRouterProvider, $locationProvider) {
    $stateProvider.state("home", {
        url : "/",
        templateUrl : "/final/CompOne.html",
        controller : "homeController"
    }).state("formula1", {
        url : "/formula1",
        templateUrl : "/final/f1.html",
        controller : "formula1Controller"
    }).state("movies", {
        url : "/movies/:movieType",
        templateUrl : "/final/movies.html",
        controller : "moviesController"
    }).state("gallery", {
        url : "/gallery/:movieId",
        templateUrl : "/templates/gallery.html",
        controller : "galleryController"
    }).state("cricket", {
        url : "/cricket/:teamId",
        templateUrl : "/final/cricket.html",
        controller : "cricketController"
    }).state("fantasy-cricket", {
        url : "/fantasy-Cricket",
        templateUrl : "/final/FantasyCricket.html",
        controller : "fantasyCricketController"
    }).state("disclaimer", {
        url : "/disclaimer",
        templateUrl : "/final/disclaimer.html",
    }).state("credits", {
        url : "/credits",
        templateUrl : "/final/credits.html",
    }).state("contact-us", {
        url : "/contact-us",
        templateUrl : "/final/contact-us.html",
        controller : "contactUsController"
    });
    $urlRouterProvider.otherwise('/'); //redirect to home screen if page not found.
});