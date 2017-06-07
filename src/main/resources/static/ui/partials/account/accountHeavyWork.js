app.controller('accountHeavyWorkCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'title', 'FileUploader',
    function ($scope, $rootScope, $timeout, $uibModalInstance, title, FileUploader) {

        $scope.title = title;

        $scope.buffer = {};

        $scope.downloadAccountExcelFile = function () {
            window.open('/api/heavy-work/account/write/' + $scope.buffer.rowCount);
        };

        $scope.uploadAccountExcelFile = function () {
            document.getElementById('input').click();
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        var uploader = $scope.uploader = new FileUploader({
            url: '/api/heavy-work/account/read/'
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

        };

    }]);