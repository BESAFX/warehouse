app.controller('branchCreateUpdateCtrl', ['BranchService', 'PersonService', 'CompanyService', 'FileUploader', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'branch',
    function (BranchService, PersonService, CompanyService, FileUploader, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, branch) {

        $timeout(function () {
            CompanyService.findAllCombo().then(function (data) {
                $scope.companies = data;
            })
        }, 1500);

        $scope.branch = branch;

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    BranchService.create($scope.branch).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    BranchService.update($scope.branch).then(function (data) {
                        $scope.branch = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadBranchLogo'
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
            $scope.branch.logo = response;
        };

    }]);