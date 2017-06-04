app.controller("companyCtrl", ['CompanyService', 'PersonService', 'ModalProvider', 'FileUploader', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (CompanyService, PersonService, ModalProvider, FileUploader, $scope, $rootScope, $log, $http, $state, $timeout) {

        $scope.selected = {};

        $scope.submit = function () {
            CompanyService.update($scope.selected).then(function (data) {
                $scope.selected = data;
            });
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadCompanyLogo'
        });

        uploader.filters.push({
            name: 'syncFilter',
            fn: function (item, options) {
                return this.queue.length < 10;
            }
        });


        uploader.filters.push({
            name: 'asyncFilter',
            fn: function (item, options, deferred) {
                setTimeout(deferred.resolve, 1e3);
            }
        });

        uploader.onAfterAddingFile = function (fileItem) {
            uploader.uploadAll();
        };

        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            $scope.selected.logo = response;
        };

        $timeout(function () {
            CompanyService.fetchTableData().then(function (data) {
                $scope.selected = data[0];
            });
            PersonService.findAllSummery().then(function (data) {
                $scope.persons = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);