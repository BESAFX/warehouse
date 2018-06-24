app.controller("menuCtrl", [
    'CompanyService',
    'CustomerService',
    'SupplierService',
    'ProductService',
    'BillPurchaseService',
    'BillPurchasePaymentService',
    'BankService',
    'BankTransactionService',
    'AttachTypeService',
    'PersonService',
    'SmsService',
    'TeamService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$state',
    '$uibModal',
    '$timeout',
    function (CompanyService,
              CustomerService,
              SupplierService,
              ProductService,
              BillPurchaseService,
              BillPurchasePaymentService,
              BankService,
              BankTransactionService,
              AttachTypeService,
              PersonService,
              SmsService,
              TeamService,
              ModalProvider,
              $scope,
              $rootScope,
              $state,
              $uibModal,
              $timeout) {

        $scope.$watch('toggleState', function (newValue, oldValue) {
            switch (newValue) {
                case 'menu': {
                    $scope.pageTitle = 'القائمة';
                    break;
                }
                case 'company': {
                    $scope.pageTitle = 'المؤسسة';
                    break;
                }
                case 'customer': {
                    $scope.pageTitle = 'العملاء';
                    break;
                }
                case 'supplier': {
                    $scope.pageTitle = 'الموردين';
                    break;
                }
                case 'product': {
                    $scope.pageTitle = 'المخزون';
                    break;
                }
                case 'billPurchase': {
                    $scope.pageTitle = 'فواتير الشراء';
                    break;
                }
                case 'billPurchasePayment': {
                    $scope.pageTitle = 'دفعات الشراء';
                    break;
                }
                case 'billSell': {
                    $scope.pageTitle = 'فواتير البيع';
                    break;
                }
                case 'billSellPayment': {
                    $scope.pageTitle = 'دفعات البيع';
                    break;
                }
                case 'bankTransaction': {
                    $scope.pageTitle = 'المعاملات المالية';
                    break;
                }
                case 'team': {
                    $scope.pageTitle = 'الصلاحيات';
                    break;
                }
                case 'person': {
                    $scope.pageTitle = 'المستخدمين';
                    break;
                }
                case 'profile': {
                    $scope.pageTitle = 'الملف الشخصي';
                    break;
                }
                case 'help': {
                    $scope.pageTitle = 'المساعدة';
                    break;
                }
                case 'about': {
                    $scope.pageTitle = 'عن البرنامج';
                    break;
                }
                case 'report': {
                    $scope.pageTitle = 'التقارير';
                    break;
                }
            }
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        }, true);
        $scope.toggleState = 'menu';
        $scope.openStateMenu = function () {
            $scope.toggleState = 'menu';
            $rootScope.refreshGUI();
        };
        $scope.openStateCompany = function () {
            $scope.toggleState = 'company';
            $rootScope.refreshGUI();
        };
        $scope.openStateCustomer = function () {
            $scope.toggleState = 'customer';
            $scope.searchCustomers({});
            $rootScope.refreshGUI();
        };
        $scope.openStateSupplier = function () {
            $scope.toggleState = 'supplier';
            $scope.searchSuppliers({});
            $rootScope.refreshGUI();
        };
        $scope.openStateProduct = function () {
            $scope.toggleState = 'product';
            $scope.searchProducts({});
            $rootScope.refreshGUI();
        };
        $scope.openStateBillPurchase = function () {
            $scope.toggleState = 'billPurchase';
            $scope.searchBillPurchases({});
            $rootScope.refreshGUI();
        };
        $scope.openStateBillPurchasePayment = function () {
            $scope.toggleState = 'billPurchasePayment';
            $scope.searchBillPurchasePayments({});
            $rootScope.refreshGUI();
        };
        $scope.openStateBillSell = function () {
            $scope.toggleState = 'billSell';
            $scope.searchBillSells({});
            $rootScope.refreshGUI();
        };
        $scope.openStateBillSellPayment = function () {
            $scope.toggleState = 'billSellPayment';
            $scope.searchBillSellPayments({});
            $rootScope.refreshGUI();
        };
        $scope.openStateBankTransaction = function () {
            $scope.toggleState = 'bankTransaction';
            $scope.searchBankTransactions({});
            $rootScope.refreshGUI();
        };
        $scope.openStateTeam = function () {
            $scope.toggleState = 'team';
            $scope.fetchTeamTableData();
            $rootScope.refreshGUI();
        };
        $scope.openStatePerson = function () {
            $scope.toggleState = 'person';
            $scope.fetchPersonTableData();
            $rootScope.refreshGUI();
        };
        $scope.openStateProfile = function () {
            $scope.toggleState = 'profile';
            $rootScope.refreshGUI();
        };
        $scope.openStateHelp = function () {
            $scope.toggleState = 'help';
            $rootScope.refreshGUI();
        };
        $scope.openStateAbout = function () {
            $scope.toggleState = 'about';
            $rootScope.refreshGUI();
        };
        $scope.openStateReport = function () {
            $scope.toggleState = 'report';
            $scope.toggleReport = 'mainReportFrame';
            $rootScope.refreshGUI();
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Company                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $rootScope.selectedCompany = {};
        $rootScope.sms = {};
        $scope.submitCompany = function () {
            CompanyService.update($rootScope.selectedCompany).then(function (data) {
                $rootScope.selectedCompany = data;
            });
        };
        $scope.browseCompanyLogo = function () {
            document.getElementById('uploader-company').click();
        };
        $scope.uploadCompanyLogo = function (files) {
            CompanyService.uploadCompanyLogo(files[0]).then(function (data) {
                $rootScope.selectedCompany.logo = data;
                CompanyService.update($rootScope.selectedCompany).then(function (data) {
                    $rootScope.selectedCompany = data;
                });
            });
        };
        $rootScope.findSmsCredit = function () {
            SmsService.getCredit().then(function (value) {
                $rootScope.sms.credit = value.GetCreditPostResult.Credit;
                $rootScope.sms.description = value.GetCreditPostResult.Description;
                return $rootScope.sms;
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Customer                                                                                                   *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.customers = [];
        $scope.paramCustomer = {};
        $scope.customers.checkAll = false;

        $scope.pageCustomer = {};
        $scope.pageCustomer.sorts = [];
        $scope.pageCustomer.page = 0;
        $scope.pageCustomer.totalPages = 0;
        $scope.pageCustomer.currentPage = $scope.pageCustomer.page + 1;
        $scope.pageCustomer.currentPageString = ($scope.pageCustomer.page + 1) + ' / ' + $scope.pageCustomer.totalPages;
        $scope.pageCustomer.size = 25;
        $scope.pageCustomer.first = true;
        $scope.pageCustomer.last = true;

        $scope.openCustomersFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/customer/customerFilter.html',
                controller: 'customerFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramCustomer) {
                $scope.searchCustomers(paramCustomer);
            }, function () {
            });
        };
        $scope.searchCustomers = function (paramCustomer) {
            var search = [];
            search.push('size=');
            search.push($scope.pageCustomer.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageCustomer.page);
            search.push('&');
            angular.forEach($scope.pageCustomer.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if (paramCustomer.codeFrom) {
                search.push('codeFrom=');
                search.push(paramCustomer.codeFrom);
                search.push('&');
            }
            if (paramCustomer.codeTo) {
                search.push('codeTo=');
                search.push(paramCustomer.codeTo);
                search.push('&');
            }
            if (paramCustomer.registerDateTo) {
                search.push('registerDateTo=');
                search.push(paramCustomer.registerDateTo.getTime());
                search.push('&');
            }
            if (paramCustomer.registerDateFrom) {
                search.push('registerDateFrom=');
                search.push(paramCustomer.registerDateFrom.getTime());
                search.push('&');
            }
            if (paramCustomer.name) {
                search.push('name=');
                search.push(paramCustomer.name);
                search.push('&');
            }
            if (paramCustomer.mobile) {
                search.push('mobile=');
                search.push(paramCustomer.mobile);
                search.push('&');
            }
            if (paramCustomer.phone) {
                search.push('phone=');
                search.push(paramCustomer.phone);
                search.push('&');
            }
            if (paramCustomer.identityNumber) {
                search.push('identityNumber=');
                search.push(paramCustomer.identityNumber);
                search.push('&');
            }
            if (paramCustomer.qualification) {
                search.push('qualification=');
                search.push(paramCustomer.qualification);
                search.push('&');
            }
            if (paramCustomer.enabled) {
                search.push('enabled=');
                search.push(paramCustomer.enabled);
                search.push('&');
            }

            CustomerService.filter(search.join("")).then(function (data) {
                $scope.customers = data.content;

                $scope.pageCustomer.currentPage = $scope.pageCustomer.page + 1;
                $scope.pageCustomer.first = data.first;
                $scope.pageCustomer.last = data.last;
                $scope.pageCustomer.number = data.number;
                $scope.pageCustomer.numberOfElements = data.numberOfElements;
                $scope.pageCustomer.size = data.size;
                $scope.pageCustomer.totalElements = data.totalElements;
                $scope.pageCustomer.totalPages = data.totalPages;
                $scope.pageCustomer.currentPageString = ($scope.pageCustomer.page + 1) + ' / ' + $scope.pageCustomer.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextCustomersPage = function () {
            $scope.pageCustomer.page++;
            $scope.searchCustomers($scope.paramCustomer);
        };
        $scope.selectPrevCustomersPage = function () {
            $scope.pageCustomer.page--;
            $scope.searchCustomers($scope.paramCustomer);
        };
        $scope.checkAllCustomers = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllCustomers input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.customers, function (customer) {
                customer.isSelected = $scope.customers.checkAll;
            });
        };
        $scope.checkCustomer = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllCustomers').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllCustomers').MaterialCheckbox.uncheck();
                }
            }
        };
        $scope.newCustomer = function () {
            ModalProvider.openCustomerCreateModel().result.then(function (data) {
                $scope.customers.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.deleteCustomer = function (customer) {
            ModalProvider.openConfirmModel("العملاء", "delete", "هل تود حذف العميل فعلاً؟").result.then(function (value) {
                if (value) {
                    CustomerService.remove(customer.id).then(function () {
                        var index = $scope.customers.indexOf(customer);
                        $scope.customers.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuCustomer = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CUSTOMER_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newCustomer();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CUSTOMER_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openCustomerUpdateModel($itemScope.customer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CUSTOMER_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteCustomer($itemScope.customer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/send.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>إرسال رسالة...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_SMS_SEND']);
                },
                click: function ($itemScope, $event, value) {
                    var customers = [];
                    angular.forEach($scope.customers, function (customer) {
                        if (customer.isSelected) {
                            customers.push(customer);
                        }
                    });
                    if (customers.length === 0) {
                        ModalProvider.openConfirmModel('إرسال الرسائل', 'send', 'فضلا قم باختيار عميل واحد على الأقل');
                        return;
                    }
                    ModalProvider.openCustomerSendMessageModel(customers);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Supplier                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.suppliers = [];
        $scope.paramSupplier = {};
        $scope.suppliers.checkAll = false;

        $scope.pageSupplier = {};
        $scope.pageSupplier.sorts = [];
        $scope.pageSupplier.page = 0;
        $scope.pageSupplier.totalPages = 0;
        $scope.pageSupplier.currentPage = $scope.pageSupplier.page + 1;
        $scope.pageSupplier.currentPageString = ($scope.pageSupplier.page + 1) + ' / ' + $scope.pageSupplier.totalPages;
        $scope.pageSupplier.size = 25;
        $scope.pageSupplier.first = true;
        $scope.pageSupplier.last = true;

        $scope.openSuppliersFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/supplier/supplierFilter.html',
                controller: 'supplierFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramSupplier) {
                $scope.searchSuppliers(paramSupplier);
            }, function () {
            });
        };
        $scope.searchSuppliers = function (paramSupplier) {
            var search = [];
            search.push('size=');
            search.push($scope.pageSupplier.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageSupplier.page);
            search.push('&');
            angular.forEach($scope.pageSupplier.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if (paramSupplier.codeFrom) {
                search.push('codeFrom=');
                search.push(paramSupplier.codeFrom);
                search.push('&');
            }
            if (paramSupplier.codeTo) {
                search.push('codeTo=');
                search.push(paramSupplier.codeTo);
                search.push('&');
            }
            if (paramSupplier.registerDateTo) {
                search.push('registerDateTo=');
                search.push(paramSupplier.registerDateTo.getTime());
                search.push('&');
            }
            if (paramSupplier.registerDateFrom) {
                search.push('registerDateFrom=');
                search.push(paramSupplier.registerDateFrom.getTime());
                search.push('&');
            }
            if (paramSupplier.name) {
                search.push('name=');
                search.push(paramSupplier.name);
                search.push('&');
            }
            if (paramSupplier.mobile) {
                search.push('mobile=');
                search.push(paramSupplier.mobile);
                search.push('&');
            }
            if (paramSupplier.phone) {
                search.push('phone=');
                search.push(paramSupplier.phone);
                search.push('&');
            }
            if (paramSupplier.identityNumber) {
                search.push('identityNumber=');
                search.push(paramSupplier.identityNumber);
                search.push('&');
            }
            if (paramSupplier.qualification) {
                search.push('qualification=');
                search.push(paramSupplier.qualification);
                search.push('&');
            }
            if (paramSupplier.enabled) {
                search.push('enabled=');
                search.push(paramSupplier.enabled);
                search.push('&');
            }

            SupplierService.filter(search.join("")).then(function (data) {
                $scope.suppliers = data.content;

                $scope.pageSupplier.currentPage = $scope.pageSupplier.page + 1;
                $scope.pageSupplier.first = data.first;
                $scope.pageSupplier.last = data.last;
                $scope.pageSupplier.number = data.number;
                $scope.pageSupplier.numberOfElements = data.numberOfElements;
                $scope.pageSupplier.size = data.size;
                $scope.pageSupplier.totalElements = data.totalElements;
                $scope.pageSupplier.totalPages = data.totalPages;
                $scope.pageSupplier.currentPageString = ($scope.pageSupplier.page + 1) + ' / ' + $scope.pageSupplier.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextSuppliersPage = function () {
            $scope.pageSupplier.page++;
            $scope.searchSuppliers($scope.paramSupplier);
        };
        $scope.selectPrevSuppliersPage = function () {
            $scope.pageSupplier.page--;
            $scope.searchSuppliers($scope.paramSupplier);
        };
        $scope.checkAllSuppliers = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllSuppliers input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.suppliers, function (supplier) {
                supplier.isSelected = $scope.suppliers.checkAll;
            });
        };
        $scope.checkSupplier = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllSuppliers').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllSuppliers').MaterialCheckbox.uncheck();
                }
            }
        };
        $scope.newSupplier = function () {
            ModalProvider.openSupplierCreateModel().result.then(function (data) {
                $scope.suppliers.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.deleteSupplier = function (supplier) {
            ModalProvider.openConfirmModel("الموردين", "delete", "هل تود حذف المورد فعلاً؟").result.then(function (value) {
                if (value) {
                    SupplierService.remove(supplier.id).then(function () {
                        var index = $scope.suppliers.indexOf(supplier);
                        $scope.suppliers.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuSupplier = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CUSTOMER_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newSupplier();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_SUPPLIER_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openSupplierUpdateModel($itemScope.supplier);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_SUPPLIER_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteSupplier($itemScope.supplier);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/send.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>إرسال رسالة...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_SMS_SEND']);
                },
                click: function ($itemScope, $event, value) {
                    var suppliers = [];
                    angular.forEach($scope.suppliers, function (supplier) {
                        if (supplier.isSelected) {
                            suppliers.push(supplier);
                        }
                    });
                    if (suppliers.length === 0) {
                        ModalProvider.openConfirmModel('إرسال الرسائل', 'send', 'فضلا قم باختيار مورد واحد على الأقل');
                        return;
                    }
                    ModalProvider.openCustomerSendMessageModel(suppliers);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Product                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.products = [];
        $scope.parents = [];
        $scope.paramProduct = {};
        $scope.products.checkAll = false;

        $scope.pageProduct = {};
        $scope.pageProduct.sorts = [];
        $scope.pageProduct.page = 0;
        $scope.pageProduct.totalPages = 0;
        $scope.pageProduct.currentPage = $scope.pageProduct.page + 1;
        $scope.pageProduct.currentPageString = ($scope.pageProduct.page + 1) + ' / ' + $scope.pageProduct.totalPages;
        $scope.pageProduct.size = 25;
        $scope.pageProduct.first = true;
        $scope.pageProduct.last = true;

        $scope.openProductsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/product/productFilter.html',
                controller: 'productFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramProduct) {
                $scope.searchProducts(paramProduct);
            }, function () {
            });
        };
        $scope.searchProducts = function (paramProduct) {
            var search = [];
            search.push('size=');
            search.push($scope.pageProduct.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageProduct.page);
            search.push('&');
            angular.forEach($scope.pageProduct.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageProduct.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            //Product Filters
            if (paramProduct.codeFrom) {
                search.push('codeFrom=');
                search.push(paramProduct.codeFrom);
                search.push('&');
            }
            if (paramProduct.codeTo) {
                search.push('codeTo=');
                search.push(paramProduct.codeTo);
                search.push('&');
            }
            if (paramProduct.registerDateFromTo) {
                search.push('registerDateFromTo=');
                search.push(paramProduct.registerDateFromTo.getTime());
                search.push('&');
            }
            if (paramProduct.registerDateFromFrom) {
                search.push('registerDateFromFrom=');
                search.push(paramProduct.registerDateFromFrom.getTime());
                search.push('&');
            }

            ProductService.filter(search.join("")).then(function (data) {
                $scope.products = data.content;

                $scope.pageProduct.currentPage = $scope.pageProduct.page + 1;
                $scope.pageProduct.first = data.first;
                $scope.pageProduct.last = data.last;
                $scope.pageProduct.number = data.number;
                $scope.pageProduct.numberOfElements = data.numberOfElements;
                $scope.pageProduct.size = data.size;
                $scope.pageProduct.totalElements = data.totalElements;
                $scope.pageProduct.totalPages = data.totalPages;
                $scope.pageProduct.currentPageString = ($scope.pageProduct.page + 1) + ' / ' + $scope.pageProduct.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextProductsPage = function () {
            $scope.pageProduct.page++;
            $scope.searchProducts($scope.paramProduct);
        };
        $scope.selectPrevProductsPage = function () {
            $scope.pageProduct.page--;
            $scope.searchProducts($scope.paramProduct);
        };
        $scope.newProduct = function () {
            ModalProvider.openProductCreateModel().result.then(function (data) {
                $scope.products.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.newParent = function () {
            ModalProvider.openParentCreateModel().result.then(function (data) {
                $scope.parents.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * BillPurchase                                                                                               *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.billPurchases = [];
        $scope.paramBillPurchase = {};
        $scope.billPurchases.checkAll = false;

        $scope.pageBillPurchase = {};
        $scope.pageBillPurchase.sorts = [];
        $scope.pageBillPurchase.page = 0;
        $scope.pageBillPurchase.totalPages = 0;
        $scope.pageBillPurchase.currentPage = $scope.pageBillPurchase.page + 1;
        $scope.pageBillPurchase.currentPageString = ($scope.pageBillPurchase.page + 1) + ' / ' + $scope.pageBillPurchase.totalPages;
        $scope.pageBillPurchase.size = 25;
        $scope.pageBillPurchase.first = true;
        $scope.pageBillPurchase.last = true;

        $scope.openBillPurchasesFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/billPurchase/billPurchaseFilter.html',
                controller: 'billPurchaseFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramBillPurchase) {
                $scope.searchBillPurchases(paramBillPurchase);
            }, function () {
            });
        };
        $scope.searchBillPurchases = function (paramBillPurchase) {
            var search = [];
            search.push('size=');
            search.push($scope.pageBillPurchase.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageBillPurchase.page);
            search.push('&');
            angular.forEach($scope.pageBillPurchase.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageBillPurchase.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            //BillPurchase Filters
            if (paramBillPurchase.codeFrom) {
                search.push('codeFrom=');
                search.push(paramBillPurchase.codeFrom);
                search.push('&');
            }
            if (paramBillPurchase.codeTo) {
                search.push('codeTo=');
                search.push(paramBillPurchase.codeTo);
                search.push('&');
            }
            if (paramBillPurchase.dateTo) {
                search.push('dateTo=');
                search.push(paramBillPurchase.dateTo.getTime());
                search.push('&');
            }
            if (paramBillPurchase.dateFrom) {
                search.push('dateFrom=');
                search.push(paramBillPurchase.dateFrom.getTime());
                search.push('&');
            }
            //Supplier Filters
            if (paramBillPurchase.supplierCodeFrom) {
                search.push('supplierCodeFrom=');
                search.push(paramBillPurchase.supplierCodeFrom);
                search.push('&');
            }
            if (paramBillPurchase.supplierCodeTo) {
                search.push('supplierCodeTo=');
                search.push(paramBillPurchase.supplierCodeTo);
                search.push('&');
            }
            if (paramBillPurchase.supplierRegisterDateTo) {
                search.push('supplierRegisterDateTo=');
                search.push(paramBillPurchase.supplierRegisterDateTo.getTime());
                search.push('&');
            }
            if (paramBillPurchase.supplierRegisterDateFrom) {
                search.push('supplierRegisterDateFrom=');
                search.push(paramBillPurchase.supplierRegisterDateFrom.getTime());
                search.push('&');
            }
            if (paramBillPurchase.supplierName) {
                search.push('supplierName=');
                search.push(paramBillPurchase.supplierName.getTime());
                search.push('&');
            }
            if (paramBillPurchase.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramBillPurchase.supplierMobile.getTime());
                search.push('&');
            }
            BillPurchaseService.filter(search.join("")).then(function (data) {
                $scope.billPurchases = data.content;

                $scope.pageBillPurchase.currentPage = $scope.pageBillPurchase.page + 1;
                $scope.pageBillPurchase.first = data.first;
                $scope.pageBillPurchase.last = data.last;
                $scope.pageBillPurchase.number = data.number;
                $scope.pageBillPurchase.numberOfElements = data.numberOfElements;
                $scope.pageBillPurchase.size = data.size;
                $scope.pageBillPurchase.totalElements = data.totalElements;
                $scope.pageBillPurchase.totalPages = data.totalPages;
                $scope.pageBillPurchase.currentPageString = ($scope.pageBillPurchase.page + 1) + ' / ' + $scope.pageBillPurchase.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextBillPurchasesPage = function () {
            $scope.pageBillPurchase.page++;
            $scope.searchBillPurchases($scope.paramBillPurchase);
        };
        $scope.selectPrevBillPurchasesPage = function () {
            $scope.pageBillPurchase.page--;
            $scope.searchBillPurchases($scope.paramBillPurchase);
        };
        $scope.checkAllBillPurchases = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllBillPurchases input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.billPurchases, function (billPurchase) {
                billPurchase.isSelected = $scope.billPurchases.checkAll;
            });
        };
        $scope.checkBillPurchase = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllBillPurchases').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllBillPurchases').MaterialCheckbox.uncheck();
                }
            }
        };
        $scope.newBillPurchase = function () {
            ModalProvider.openBillPurchaseCreateModel().result.then(function (data) {
                $scope.billPurchases.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.newOldBillPurchase = function () {
            ModalProvider.openBillPurchaseOldCreateModel().result.then(function (data) {
                $scope.billPurchases.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.deleteBillPurchase = function (billPurchase) {
            ModalProvider.openConfirmModel("العقود", "delete", "هل تود حذف العقد فعلاً؟").result.then(function (value) {
                if (value) {
                    BillPurchaseService.remove(billPurchase.id).then(function () {
                        var index = $scope.billPurchases.indexOf(billPurchase);
                        $scope.billPurchases.splice(index, 1);
                    });
                }
            });
        };
        $scope.newBillPurchaseProduct = function (billPurchase) {
            ModalProvider.openBillPurchaseProductCreateModel(billPurchase).result.then(function (data) {
                if (!billPurchase.billPurchaseProducts) {
                    billPurchase.billPurchaseProducts = [];
                }
                return billPurchase.billPurchaseProducts.splice(0, 0, data);
            });
        };
        $scope.newBillPurchasePayment = function (billPurchase) {
            ModalProvider.openBillPurchasePaymentCreateModel(billPurchase).result.then(function (data) {
                if (!billPurchase.billPurchasePayments) {
                    billPurchase.billPurchasePayments = [];
                }
                return billPurchase.billPurchasePayments.splice(0, 0, data);
            });
        };
        $scope.rowMenuBillPurchase = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BILL_PURCHASE_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBillPurchase();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BILL_PURCHASE_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteBillPurchase($itemScope.billPurchase);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/product.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>اضافة سلعة...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BILL_PURCHASE_PRODUCT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBillPurchaseProduct($itemScope.billPurchase);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/bankTransaction.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تسديد دفعة مالية...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BILL_PURCHASE_PAYMENT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBillPurchasePayment($itemScope.billPurchase);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/about.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>التفاصيل...</span>' +
                '</div>',
                enabled: function () {
                    return true;
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBillPurchaseDetailsModel($itemScope.billPurchase);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * BillPurchasePayment                                                                                        *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.billPurchasePayments = [];
        $scope.paramBillPurchasePayment = {};
        $scope.billPurchasePayments.checkAll = false;

        $scope.pageBillPurchasePayment = {};
        $scope.pageBillPurchasePayment.sorts = [];
        $scope.pageBillPurchasePayment.page = 0;
        $scope.pageBillPurchasePayment.totalPages = 0;
        $scope.pageBillPurchasePayment.currentPage = $scope.pageBillPurchasePayment.page + 1;
        $scope.pageBillPurchasePayment.currentPageString = ($scope.pageBillPurchasePayment.page + 1) + ' / ' + $scope.pageBillPurchasePayment.totalPages;
        $scope.pageBillPurchasePayment.size = 25;
        $scope.pageBillPurchasePayment.first = true;
        $scope.pageBillPurchasePayment.last = true;

        $scope.openBillPurchasePaymentsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/billPurchasePayment/billPurchasePaymentFilter.html',
                controller: 'billPurchasePaymentFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramBillPurchasePayment) {
                $scope.searchBillPurchasePayments(paramBillPurchasePayment);
            }, function () {
            });
        };
        $scope.searchBillPurchasePayments = function (paramBillPurchasePayment) {
            var search = [];
            search.push('size=');
            search.push($scope.pageBillPurchasePayment.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageBillPurchasePayment.page);
            search.push('&');
            angular.forEach($scope.pageBillPurchasePayment.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageBillPurchasePayment.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            if (paramBillPurchasePayment.dateFrom) {
                search.push('dateFrom=');
                search.push(paramBillPurchasePayment.dateFrom.getTime());
                search.push('&');
            }
            if (paramBillPurchasePayment.dateTo) {
                search.push('dateTo=');
                search.push(paramBillPurchasePayment.dateTo.getTime());
                search.push('&');
            }
            if (paramBillPurchasePayment.billPurchaseCodeFrom) {
                search.push('billPurchaseCodeFrom=');
                search.push(paramBillPurchasePayment.billPurchaseCodeFrom);
                search.push('&');
            }
            if (paramBillPurchasePayment.billPurchaseCodeTo) {
                search.push('billPurchaseCodeTo=');
                search.push(paramBillPurchasePayment.billPurchaseCodeTo);
                search.push('&');
            }
            if (paramBillPurchasePayment.billPurchaseDateFrom) {
                search.push('billPurchaseDateFrom=');
                search.push(paramBillPurchasePayment.billPurchaseDateFrom.getTime());
                search.push('&');
            }
            if (paramBillPurchasePayment.billPurchaseDateTo) {
                search.push('billPurchaseDateTo=');
                search.push(paramBillPurchasePayment.billPurchaseDateTo.getTime());
                search.push('&');
            }
            if (paramBillPurchasePayment.supplierName) {
                search.push('supplierName=');
                search.push(paramBillPurchasePayment.supplierName);
                search.push('&');
            }
            if (paramBillPurchasePayment.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramBillPurchasePayment.supplierMobile);
                search.push('&');
            }

            search.push('filterCompareType=or');

            BillPurchasePaymentService.filter(search.join("")).then(function (data) {
                $scope.billPurchasePayments = data.content;

                $scope.pageBillPurchasePayment.currentPage = $scope.pageBillPurchasePayment.page + 1;
                $scope.pageBillPurchasePayment.first = data.first;
                $scope.pageBillPurchasePayment.last = data.last;
                $scope.pageBillPurchasePayment.number = data.number;
                $scope.pageBillPurchasePayment.numberOfElements = data.numberOfElements;
                $scope.pageBillPurchasePayment.size = data.size;
                $scope.pageBillPurchasePayment.totalElements = data.totalElements;
                $scope.pageBillPurchasePayment.totalPages = data.totalPages;
                $scope.pageBillPurchasePayment.currentPageString = ($scope.pageBillPurchasePayment.page + 1) + ' / ' + $scope.pageBillPurchasePayment.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextBillPurchasePaymentsPage = function () {
            $scope.pageBillPurchasePayment.page++;
            $scope.searchBillPurchasePayments($scope.paramBillPurchasePayment);
        };
        $scope.selectPrevBillPurchasePaymentsPage = function () {
            $scope.pageBillPurchasePayment.page--;
            $scope.searchBillPurchasePayments($scope.paramBillPurchasePayment);
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * BankTransaction                                                                                            *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.bankTransactions = [];
        $scope.paramBankTransaction = {};
        $scope.paramBankTransaction.transactionTypeCodes = [];
        $scope.bankTransactions.checkAll = false;

        $scope.pageBankTransaction = {};
        $scope.pageBankTransaction.sorts = [];
        $scope.pageBankTransaction.page = 0;
        $scope.pageBankTransaction.totalPages = 0;
        $scope.pageBankTransaction.currentPage = $scope.pageBankTransaction.page + 1;
        $scope.pageBankTransaction.currentPageString = ($scope.pageBankTransaction.page + 1) + ' / ' + $scope.pageBankTransaction.totalPages;
        $scope.pageBankTransaction.size = 25;
        $scope.pageBankTransaction.first = true;
        $scope.pageBankTransaction.last = true;

        $scope.openBankTransactionsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/bankTransaction/bankTransactionFilter.html',
                controller: 'bankTransactionFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramBankTransaction) {
                $scope.searchBankTransactions(paramBankTransaction);
            }, function () {
            });
        };
        $scope.searchBankTransactions = function (paramBankTransaction) {
            var search = [];
            search.push('size=');
            search.push($scope.pageBankTransaction.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageBankTransaction.page);
            search.push('&');
            angular.forEach($scope.pageBankTransaction.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageBankTransaction.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            if (paramBankTransaction.codeFrom) {
                search.push('codeFrom=');
                search.push(paramBankTransaction.codeFrom);
                search.push('&');
            }
            if (paramBankTransaction.codeTo) {
                search.push('codeTo=');
                search.push(paramBankTransaction.codeTo);
                search.push('&');
            }
            if (paramBankTransaction.dateTo) {
                search.push('dateTo=');
                search.push(paramBankTransaction.dateTo.getTime());
                search.push('&');
            }
            if (paramBankTransaction.dateFrom) {
                search.push('dateFrom=');
                search.push(paramBankTransaction.dateFrom.getTime());
                search.push('&');
            }
            if (paramBankTransaction.supplierName) {
                search.push('supplierName=');
                search.push(paramBankTransaction.supplierName);
                search.push('&');
            }
            if (paramBankTransaction.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramBankTransaction.supplierMobile);
                search.push('&');
            }
            if (paramBankTransaction.supplierIdentityNumber) {
                search.push('supplierIdentityNumber=');
                search.push(paramBankTransaction.supplierIdentityNumber);
                search.push('&');
            }
            if (paramBankTransaction.transactionTypeCode) {
                search.push('transactionTypeCodes=');
                search.push([paramBankTransaction.transactionTypeCode]);
                search.push('&');
            }

            BankTransactionService.filter(search.join("")).then(function (data) {
                $scope.bankTransactions = data.content;

                $scope.pageBankTransaction.currentPage = $scope.pageBankTransaction.page + 1;
                $scope.pageBankTransaction.first = data.first;
                $scope.pageBankTransaction.last = data.last;
                $scope.pageBankTransaction.number = data.number;
                $scope.pageBankTransaction.numberOfElements = data.numberOfElements;
                $scope.pageBankTransaction.size = data.size;
                $scope.pageBankTransaction.totalElements = data.totalElements;
                $scope.pageBankTransaction.totalPages = data.totalPages;
                $scope.pageBankTransaction.currentPageString = ($scope.pageBankTransaction.page + 1) + ' / ' + $scope.pageBankTransaction.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextBankTransactionsPage = function () {
            $scope.pageBankTransaction.page++;
            $scope.searchBankTransactions($scope.paramBankTransaction);
        };
        $scope.selectPrevBankTransactionsPage = function () {
            $scope.pageBankTransaction.page--;
            $scope.searchBankTransactions($scope.paramBankTransaction);
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Person                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.persons = [];
        $scope.fetchPersonTableData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
            });
        };
        $scope.newPerson = function () {
            ModalProvider.openPersonCreateModel().result.then(function (data) {
                return $scope.persons.splice(0, 0, data);
            });
        };
        $scope.deletePerson = function (person) {
            ModalProvider.openConfirmModel("المستخدمين", "delete", "هل تود حذف المستخدم فعلاً؟").result.then(function (value) {
                if (value) {
                    PersonService.remove(person.id).then(function () {
                        var index = $scope.persons.indexOf(person);
                        $scope.persons.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuPerson = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newPerson();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openPersonUpdateModel($itemScope.person);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deletePerson($itemScope.person);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Team                                                                                                       *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.fetchTeamTableData = function () {
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
            });
        };
        $scope.newTeam = function () {
            ModalProvider.openTeamCreateModel().result.then(function (data) {
                return $scope.teams.splice(0, 0, data);
            });
        };
        $scope.deleteTeam = function (team) {
            ModalProvider.openConfirmModel("الصلاحيات", "delete", "هل تود حذف المجموعة فعلاً؟").result.then(function (value) {
                if (value) {
                    TeamService.remove(team.id).then(function () {
                        var index = $scope.teams.indexOf(team);
                        $scope.teams.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuTeam = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newTeam();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openTeamUpdateModel($itemScope.team);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteTeam($itemScope.team);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Profile                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.submitProfile = function () {
            PersonService.updateProfile($rootScope.me).then(function (data) {
                $rootScope.me = data;
            });
        };
        $scope.browseProfilePhoto = function () {
            document.getElementById('uploader-profile').click();
        };
        $scope.uploadProfilePhoto = function (files) {
            PersonService.uploadContactPhoto(files[0]).then(function (data) {
                $rootScope.me.logo = data;
                PersonService.update($rootScope.me).then(function (data) {
                    $rootScope.me = data;
                });
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Print                                                                                                      *
         *                                                                                                            *
         **************************************************************************************************************/
        $rootScope.printToCart = function (printSectionId, title) {
            var innerContents = document.getElementById(printSectionId).innerHTML;
            var mywindow = window.open(title, '_blank', 'height=400,width=600');
            mywindow.document.write('<html><head><title></title>');
            mywindow.document.write('<link rel="stylesheet" href="/ui/app.css" type="text/css" />');
            mywindow.document.write('<link rel="stylesheet" href="/ui/css/style.css" type="text/css" />');
            mywindow.document.write('</head><body >');
            mywindow.document.write(innerContents);
            mywindow.document.write('</body></html>');
            mywindow.document.close();
            mywindow.focus();
            $timeout(function () {
                mywindow.print();
                mywindow.close();
            }, 1000);
            return true;
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Report                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.toggleReport = 'mainReportFrame';
        //السيولة النقدية
        $scope.openReportCashBalance = function () {
            $scope.toggleReport = 'cashBalance';
            $rootScope.refreshGUI();
        };
        //تقرير المصروفات
        $scope.openReportWithdrawCash = function () {
            $scope.toggleReport = 'withdrawCash';
            $rootScope.refreshGUI();
        };
        //أرصدة الموردين
        $scope.openReportSupplierBalance = function () {
            $scope.toggleReport = 'supplierBalance';
            $rootScope.refreshGUI();
        };
        //تقرير العقود
        $scope.openReportBillPurchases = function () {
            $scope.toggleReport = 'billPurchases';
            $rootScope.refreshGUI();
        };
        //حركة العمليات اليومية
        $scope.openReportBankTransactions = function () {
            $scope.toggleReport = 'bankTransactions';
            $rootScope.refreshGUI();
        };
        //كشف حساب مورد
        $scope.openReportSupplierStatement = function () {
            $scope.toggleReport = 'supplierStatement';
            $rootScope.refreshGUI();
        };
        //كشف حساب عميل
        $scope.openReportCustomerStatement = function () {
            $scope.toggleReport = 'customerStatement';
            $rootScope.refreshGUI();
        };
        //تقرير التحصيل والسداد
        $scope.openReportBillPurchasePremiums = function () {
            $scope.toggleReport = 'billPurchasePremiums';
            $rootScope.refreshGUI();
        };
        //تقرير أرباح التحصيل
        $scope.openReportBillPurchasePaymentsProfit = function () {
            $scope.toggleReport = 'billPurchasePaymentsProfit';
            $rootScope.refreshGUI();
        };

        $scope.paramWithdrawCash = {};
        $scope.supplierStatement = {};
        $scope.customerStatement = {};
        $scope.billPurchasePaymentProfit = {};
        $scope.fetchAllBanks = function () {
            BankService.findAll().then(function (value) {
                $scope.banks = value;
            });
        };
        $scope.fetchWithdrawCashes = function () {
            var search = [];
            if ($scope.paramWithdrawCash.dateTo) {
                search.push('dateTo=');
                search.push($scope.paramWithdrawCash.dateTo.getTime());
                search.push('&');
            }
            if ($scope.paramWithdrawCash.dateFrom) {
                search.push('dateFrom=');
                search.push($scope.paramWithdrawCash.dateFrom.getTime());
                search.push('&');
            }

            search.push('transactionTypeCodes=');
            search.push(['Withdraw_Cash']);

            BankTransactionService.findByDateBetweenOrTransactionTypeCodeIn(search.join("")).then(function (data) {
                $scope.withdrawCashes = data;
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.fetchAllSupplierBalance = function () {
            SupplierService.findAllSupplierBalance().then(function (value) {
                $scope.suppliersBalance = value;
            })
        };
        $scope.fetchAllSupplierCombo = function () {
            SupplierService.findAllCombo().then(function (value) {
                $scope.suppliersCombo = value;
            });
        };
        $scope.fetchAllCustomerCombo = function () {
            CustomerService.findAllCombo().then(function (value) {
                $scope.customersCombo = value;
            });
        };
        $scope.fetchSupplierStatementBillPurchases = function () {
            BillPurchaseService.findBySupplier($scope.supplierStatement.supplier.id).then(function (value) {
                $scope.supplierStatement.billPurchases = value;
            });
        };
        $scope.fetchSupplierStatementBanks = function () {
            BankService.findBySupplier($scope.supplierStatement.supplier.id).then(function (value) {
                $scope.supplierStatement.banks = value;
            });
        };
        $scope.fetchBillPurchasePaymentsProfit = function () {
            BillPurchasePaymentService.findByDateBetween(
                $scope.billPurchasePaymentProfit.dateFrom.getTime(),
                $scope.billPurchasePaymentProfit.dateTo.getTime()
            )
                .then(function (value) {
                    $scope.billPurchasePaymentProfit.billPurchasePayments = value;
                });
        };
        $scope.findCustomerDetails = function () {
            CustomerService.findOne($scope.customerStatement.customer.id).then(function (value) {
                return $scope.customerStatement.customer = value;
            });
        };

        $timeout(function () {
            CompanyService.get().then(function (data) {
                $rootScope.selectedCompany = data;
            });
            PersonService.findAllCombo().then(function (data) {
                $scope.personsCombo = data;
            });
            AttachTypeService.findAll().then(function (data) {
                $scope.attachTypes = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);