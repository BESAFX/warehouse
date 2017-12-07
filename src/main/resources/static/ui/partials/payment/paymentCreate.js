app.controller('paymentCreateCtrl', ['BranchService', 'MasterService', 'CourseService', 'AccountService', 'PaymentService', 'PaymentAttachService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, MasterService, CourseService, AccountService, PaymentService, PaymentAttachService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.selected = {};

        $scope.payment = {};

        $scope.buffer = {};
        $scope.buffer.branchList = [];
        $scope.buffer.masterList = [];
        $scope.buffer.courseList = [];

        $scope.hijriDate = true;

        $scope.accounts = [];
        $scope.files = [];

        $scope.radio = {};
        $scope.radio.registerOption = '1';

        $scope.title = title;

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.accounts, function (account) {
                    if (object.id == account.id) {
                        AccountService.findOne(account.id).then(function (data) {
                            account = data;
                            $scope.selected = data;
                            $scope.payment.account = data;
                        });
                        return account.isSelected = true;
                    } else {
                        return account.isSelected = false;
                    }
                });
            }
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            });
            CourseService.fetchCourseMasterBranchCombo().then(function (data) {
                $scope.courses = data;
            });
        }, 1000);

        $scope.search = function () {
            var search = [];
            switch ($scope.radio.registerOption){
                case '1':
                    //
                    if (!$scope.buffer.branchList || $scope.buffer.branchList.length === 0) {
                        $scope.buffer = {};
                        $scope.accounts = [];
                        return;
                    }
                    var branchIds = [];
                    angular.forEach($scope.buffer.branchList, function (branch) {
                        branchIds.push(branch.id);
                    });
                    search.push('branchIds=');
                    search.push(branchIds);
                    search.push('&');
                    //
                    break;
                case '2':
                    //
                    if (!$scope.buffer.masterList || $scope.buffer.masterList.length === 0) {
                        $scope.buffer = {};
                        $scope.accounts = [];
                        return;
                    }
                    var masterIds = [];
                    angular.forEach($scope.buffer.masterList, function (master) {
                        masterIds.push(master.id);
                    });
                    search.push('masterIds=');
                    search.push(masterIds);
                    search.push('&');
                    //
                    break;
                case '3':
                    //
                    if (!$scope.buffer.courseList || $scope.buffer.courseList.length === 0) {
                        $scope.buffer = {};
                        $scope.accounts = [];
                        return;
                    }
                    var courseIds = [];
                    angular.forEach($scope.buffer.courseList, function (course) {
                        courseIds.push(course.id);
                    });
                    search.push('courseIds=');
                    search.push(courseIds);
                    search.push('&');
                    //
                    break;
            }

            //
            if ($scope.buffer.firstName) {
                search.push('firstName=');
                search.push($scope.buffer.firstName);
                search.push('&');
            }
            if ($scope.buffer.secondName) {
                search.push('secondName=');
                search.push($scope.buffer.secondName);
                search.push('&');
            }
            if ($scope.buffer.thirdName) {
                search.push('thirdName=');
                search.push($scope.buffer.thirdName);
                search.push('&');
            }
            if ($scope.buffer.forthName) {
                search.push('forthName=');
                search.push($scope.buffer.forthName);
                search.push('&');
            }
            if ($scope.buffer.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push($scope.buffer.studentIdentityNumber);
                search.push('&');
            }
            if ($scope.buffer.studentMobile) {
                search.push('studentMobile=');
                search.push($scope.buffer.studentMobile);
                search.push('&');
            }
            AccountService.filterWithInfo(search.join("")).then(function (data) {
                $scope.accounts = data;
                $scope.setSelected(data[0]);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
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
        $scope.scanToJpg = function() {
            scanner.scan(displayImagesOnPage,
                {
                    "output_settings" :
                        [
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

            return new Blob([ia], {type:mimeString});
        }

        /** Processes the scan result */
        function displayImagesOnPage(successful, mesg, response) {
            var scannedImages = scanner.getScannedImages(response, true, false); // returns an array of ScannedImage
            for(var i = 0; (scannedImages instanceof Array) && i < scannedImages.length; i++) {
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