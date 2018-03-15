app.controller('branchCreateUpdateCtrl', ['BranchService', 'FileUploader', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'branch',
    function (BranchService, FileUploader, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, branch) {

        $scope.branch = branch;

        $scope.title = title;

        $scope.action = action;

        if($scope.branch.id){
            $timeout(function () {
                BranchService.findOne($scope.branch.id).then(function (data) {
                    $scope.branch = data;
                });
            }, 1500);
        }

        $scope.submit = function () {
            if($scope.currentFile){
                BranchService.uploadBranchLogo($scope.currentFile).then(function (data) {
                    $scope.branch.logo = data;
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
                });
            }else{
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
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.uploadFile = function () {
            document.getElementById('uploader').click();
        };

        $scope.setFile = function(element) {
            $scope.currentFile = element.files[0];
            var reader = new FileReader();

            reader.onload = function(event) {
                $scope.branch.logo = event.target.result;
                $scope.$apply();

            };
            // when the file is read it triggers the onload event above.
            reader.readAsDataURL(element.files[0]);
        }

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);