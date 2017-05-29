app.controller("studentCtrl", ['StudentService', 'ModalProvider', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (StudentService, ModalProvider, $scope, $rootScope, $log, $http, $state, $timeout) {

        $timeout(function () {

            $scope.student = {};

            StudentService.findAll().then(function (data) {
                $scope.students = data;
            });

        }, 2000);

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openStudentCreateModel();
        };

        $scope.openUpdateModel = function (student) {
            ModalProvider.openStudentUpdateModel(student);
        };

        /**************************************************************
         *                                                            *
         * Sort                                                       *
         *                                                            *
         *************************************************************/
        $scope.propertyName = 'code';
        $scope.reverse = true;
        $scope.sortBy = function (propertyName) {
            $scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
            $scope.propertyName = propertyName;
        };
    }]);