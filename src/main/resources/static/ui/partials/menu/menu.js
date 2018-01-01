app.controller("menuCtrl", ['$scope', '$rootScope', '$state', '$timeout', function ($scope, $rootScope, $state, $timeout) {

    $(function (){
        $('.simple-marquee-container').SimpleMarquee();
    });

    $timeout(function () {
        window.componentHandler.upgradeAllRegistered();
    }, 1500);
}]);