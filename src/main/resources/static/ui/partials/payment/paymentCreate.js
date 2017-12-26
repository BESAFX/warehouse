app.controller('paymentCreateCtrl', ['BranchService', 'AccountService', 'PaymentService', 'PaymentAttachService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, AccountService, PaymentService, PaymentAttachService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.payment = {};

        $scope.buffer = {};

        $scope.accounts = [];

        $scope.files = [];

        $scope.title = title;

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1000);

        $scope.onBranchSelect = function () {
            AccountService.findByBranchWithKey($scope.buffer.branch.id).then(function (data) {
                $scope.accounts = data;
            });
        };

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                /**UPLOADING FILE**/
                // PaymentAttachService.upload(data, $scope.files[0]).then(function (data) {
                //     /**FINISHING UPLOADING**/
                // });
                /**REFRESH ACCOUNT OBJECT**/
                AccountService.findOne(data.account.id).then(function (data) {
                    var index = $scope.accounts.indexOf(data.account);
                    $scope.accounts[index] = data;
                    $scope.payment = {};
                    $scope.files = [];
                    $scope.payment.account = data;
                    $scope.form.$setPristine();
                });
            });
        };

        //////////////////////////File Manager///////////////////////////////////
        $scope.uploadFiles = function () {
            document.getElementById('uploader').click();
        };
        //////////////////////////File Manager///////////////////////////////////

        //////////////////////////Scan Manager///////////////////////////////////
        $scope.scanToJpg = function () {
            scanner.scan(displayImagesOnPage,
                {
                    "output_settings": [
                        {
                            "type": "return-base64",
                            "format": "jpg"
                        }
                    ]
                }
            );
        };

        function dataURItoBlob(dataURI) {
            // convert base64/URLEncoded data component to raw binary data held in a string
            var byteString;
            if (dataURI.split(',')[0].indexOf('base64') >= 0)
                byteString = atob(dataURI.split(',')[1]);
            else
                byteString = unescape(dataURI.split(',')[1]);

            // separate out the mime component
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

            // write the bytes of the string to a typed array
            var ia = new Uint8Array(byteString.length);
            for (var i = 0; i < byteString.length; i++) {
                ia[i] = byteString.charCodeAt(i);
            }

            return new Blob([ia], {type: mimeString});
        }

        /** Processes the scan result */
        function displayImagesOnPage(successful, mesg, response) {
            var scannedImages = scanner.getScannedImages(response, true, false); // returns an array of ScannedImage
            for (var i = 0; (scannedImages instanceof Array) && i < scannedImages.length; i++) {
                var scannedImage = scannedImages[i];
                var blob = dataURItoBlob(scannedImage.src);
                var file = new File([blob], Math.floor((Math.random() * 50000) + 1) + '.jpg');
                $scope.files.push(file);
            }
        }

        //////////////////////////Scan Manager///////////////////////////////////

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);