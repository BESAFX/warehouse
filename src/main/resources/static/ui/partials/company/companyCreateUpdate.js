app.controller('companyCreateUpdateCtrl', ['CompanyService', 'PersonService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'company',
    function (CompanyService, PersonService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, company) {

        $timeout(function () {
            PersonService.findAllSummery().then(function (data) {
                $scope.persons = data;
            })
        }, 1500);

        if (company) {
            $scope.company = company;
        } else {
            $scope.company = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    CompanyService.create($scope.company).then(function (data) {
                        $scope.company = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    CompanyService.update($scope.company).then(function (data) {
                        $scope.company = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
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
            $scope.company.logo = response;
        };

    }]);