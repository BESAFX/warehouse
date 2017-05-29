app.controller('personCreateUpdateCtrl', ['TeamService', 'BranchService', 'PersonService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'personObject',
    function (TeamService, BranchService, PersonService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, personObject) {

        $timeout(function () {

            $scope.teams = [];

            TeamService.findAll().then(function (data) {
                $scope.teams = data;
            });

            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
            });

        }, 2000);

        if (personObject) {

            $scope.personObject = personObject;

            if (personObject.contact) {
                FileService.getSharedLink(personObject.contact.photo).then(function (data) {
                    $scope.logoLink = data;
                });
            }

        } else {
            $scope.personObject = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    PersonService.create($scope.personObject).then(function (data) {
                        $scope.personObject = {};
                        $scope.from.$setPristine();
                    });
                    break;
                case 'update' :
                    PersonService.update($scope.personObject).then(function (data) {
                        $scope.personObject = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadFile'
        });

        // a sync filter
        uploader.filters.push({
            name: 'syncFilter',
            fn: function (item /*{File|FileLikeObject}*/, options) {
                console.log('syncFilter');
                return this.queue.length < 10;
            }
        });

        // an async filter
        uploader.filters.push({
            name: 'asyncFilter',
            fn: function (item /*{File|FileLikeObject}*/, options, deferred) {
                console.log('asyncFilter');
                setTimeout(deferred.resolve, 1e3);
            }
        });

        uploader.onAfterAddingFile = function (fileItem) {
            console.info('onAfterAddingFile', fileItem);
            uploader.uploadAll();
        };
        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            console.info('onSuccessItem', fileItem, response, status, headers);
            $scope.personObject.contact.photo = response;
            FileService.getSharedLink(response).then(function (data) {
                $scope.logoLink = data;
            });
        };

    }]);