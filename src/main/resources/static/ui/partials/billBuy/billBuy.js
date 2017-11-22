app.controller("billBuyCtrl", ['BranchService', 'BillBuyService', 'BillBuyTypeService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$state', '$uibModal',
    function (BranchService, BillBuyService, BillBuyTypeService, ModalProvider, $scope, $rootScope, $timeout, $state, $uibModal) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'فواتير الشراء'});
        //

        $scope.selected = {};

        $scope.buffer = {};

        $scope.billBuys = [];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
            });
        }, 2000);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.billBuys, function (billBuy) {
                    if (object.id == billBuy.id) {
                        $scope.selected = billBuy;
                        return billBuy.isSelected = true;
                    } else {
                        return billBuy.isSelected = false;
                    }
                });
            }
        };

        $scope.newBillBuy = function () {
            ModalProvider.openBillBuyCreateModel().result.then(function (data) {
                $scope.billBuys.splice(0, 0, data);
                $scope.setSelected(data);
            }, function () {
                console.info('BillBuyCreateModel Closed.');
            });
        };

            $scope.createFastBill = function () {
            BillBuyService.create($scope.billBuy).then(function (data) {
                $scope.billBuys.splice(0, 0, data);
                $scope.billBuy = {};
                $scope.form.$setPristine();
                $scope.setSelected(data);
            });
        };

        $scope.delete = function (billBuy) {
            if (billBuy) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفاتورة فعلاً؟", "error", "fa-ban", function () {
                    BillBuyService.remove(billBuy.id).then(function () {
                        var index = $scope.billBuys.indexOf(billBuy);
                        $scope.billBuys.splice(index, 1);
                        $scope.setSelected($scope.billBuys[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفاتورة فعلاً؟", "error", "fa-ban", function () {
                BillBuyService.remove($scope.selected.id).then(function () {
                    var index = $scope.billBuys.indexOf($scope.selected);
                    $scope.billBuys.splice(index, 1);
                    $scope.setSelected($scope.billBuys[0]);
                });
            });
        };

        $scope.search = function () {

            var search = [];

            if ($scope.buffer.codeFrom) {
                search.push('codeFrom=');
                search.push($scope.buffer.codeFrom);
                search.push('&');
            }
            if ($scope.buffer.codeTo) {
                search.push('codeTo=');
                search.push($scope.buffer.codeTo);
                search.push('&');
            }
            if ($scope.buffer.dateFrom) {
                search.push('dateFrom=');
                search.push($scope.buffer.dateFrom.getTime());
                search.push('&');
            }
            if ($scope.buffer.dateTo) {
                search.push('dateTo=');
                search.push($scope.buffer.dateTo.getTime());
                search.push('&');
            }
            if ($scope.buffer.amountFrom) {
                search.push('amountFrom=');
                search.push($scope.buffer.amountFrom);
                search.push('&');
            }
            if ($scope.buffer.amountTo) {
                search.push('amountTo=');
                search.push($scope.buffer.amountTo);
                search.push('&');
            }
            if ($scope.buffer.branch) {
                search.push('branchId=');
                search.push($scope.buffer.branch.id);
                search.push('&');
            }
            BillBuyService.filter(search.join("")).then(function (data) {
                $scope.billBuys = data;
                $scope.setSelected(data[0]);
                $scope.items = [];
                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.items.push({'id': 2, 'type': 'title', 'name': 'فواتير الشراء', 'style': 'font-weight:bold'});
                $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.items.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
                });
            });

        };

        $scope.clearBuffer = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
        };

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/billBuy/billBuyFilter.html',
                controller: 'billBuyFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى فواتير الشراء';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {

                $scope.buffer = buffer;

                $scope.search();

            }, function () {
                console.info('BillBuyFilterModel Closed.');
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء فاتورة جديدة <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBillBuy();
                }
            },
            {
                html: '<div class="drop-menu">تعديل بيانات الفاتورة<span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BILL_BUY_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBillBuyUpdateModel($itemScope.billBuy);
                }
            },
            {
                html: '<div class="drop-menu"> حذف الفاتورة <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.billBuy);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);