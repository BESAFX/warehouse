app.controller("menuCtrl", [
    'CompanyService',
    'CustomerService',
    'SupplierService',
    'ProductPurchaseService',
    'ContractService',
    'ContractPremiumService',
    'ContractPaymentService',
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
              ProductPurchaseService,
              ContractService,
              ContractPremiumService,
              ContractPaymentService,
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
                case 'productPurchase': {
                    $scope.pageTitle = 'المخزون';
                    break;
                }
                case 'contract': {
                    $scope.pageTitle = 'العقود';
                    break;
                }
                case 'contractPremium': {
                    $scope.pageTitle = 'الاقساط';
                    break;
                }
                case 'contractPayment': {
                    $scope.pageTitle = 'الدفعات';
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
        $scope.openStateProductPurchase = function () {
            $scope.toggleState = 'productPurchase';
            $scope.searchProductPurchases({});
            $rootScope.refreshGUI();
        };
        $scope.openStateContract = function () {
            $scope.toggleState = 'contract';
            $scope.searchContracts({});
            $rootScope.refreshGUI();
        };
        $scope.openStateContractPremium = function () {
            $scope.toggleState = 'contractPremium';
            $scope.searchContractPremiums({});
            $rootScope.refreshGUI();
        };
        $scope.openStateContractPayment = function () {
            $scope.toggleState = 'contractPayment';
            $scope.searchContractPayments({});
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
        $scope.findMyContracts = function () {
            ContractService.findMyContracts().then(function (value) {
                $scope.myContracts = value;
            });
        };
        $scope.findMyBanks = function () {
            BankService.findMyBanks().then(function (value) {
                $scope.myBanks = value;
            });
        };
        $scope.findMyBankTransactions = function () {
            BankTransactionService.findMyBankTransactions().then(function (value) {
                $scope.myBankTransactions = value;
            });
        };
        $scope.findMyProductPurchases = function () {
            ProductPurchaseService.findMyProductPurchases().then(function (value) {
                $scope.myProductPurchases = value;
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
                        ModalProvider.openConfirmModel('إرسال رسائل الأقساط', 'send', 'فضلا قم باختيار قسط واحد على الأقل');
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
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * ProductPurchase                                                                                            *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.productPurchases = [];
        $scope.paramProductPurchase = {};
        $scope.productPurchases.checkAll = false;

        $scope.pageProductPurchase = {};
        $scope.pageProductPurchase.sorts = [];
        $scope.pageProductPurchase.page = 0;
        $scope.pageProductPurchase.totalPages = 0;
        $scope.pageProductPurchase.currentPage = $scope.pageProductPurchase.page + 1;
        $scope.pageProductPurchase.currentPageString = ($scope.pageProductPurchase.page + 1) + ' / ' + $scope.pageProductPurchase.totalPages;
        $scope.pageProductPurchase.size = 25;
        $scope.pageProductPurchase.first = true;
        $scope.pageProductPurchase.last = true;

        $scope.openProductPurchasesFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/productPurchase/productPurchaseFilter.html',
                controller: 'productPurchaseFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramProductPurchase) {
                $scope.searchProductPurchases(paramProductPurchase);
            }, function () {
            });
        };
        $scope.searchProductPurchases = function (paramProductPurchase) {
            var search = [];
            search.push('size=');
            search.push($scope.pageProductPurchase.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageProductPurchase.page);
            search.push('&');
            angular.forEach($scope.pageProductPurchase.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageProductPurchase.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            //ProductPurchase Filters
            if (paramProductPurchase.codeFrom) {
                search.push('codeFrom=');
                search.push(paramProductPurchase.codeFrom);
                search.push('&');
            }
            if (paramProductPurchase.codeTo) {
                search.push('codeTo=');
                search.push(paramProductPurchase.codeTo);
                search.push('&');
            }
            if (paramProductPurchase.dateTo) {
                search.push('dateTo=');
                search.push(paramProductPurchase.dateTo.getTime());
                search.push('&');
            }
            if (paramProductPurchase.dateFrom) {
                search.push('dateFrom=');
                search.push(paramProductPurchase.dateFrom.getTime());
                search.push('&');
            }
            //Product Filters
            if (paramProductPurchase.productCodeFrom) {
                search.push('productCodeFrom=');
                search.push(paramProductPurchase.productCodeFrom);
                search.push('&');
            }
            if (paramProductPurchase.productCodeTo) {
                search.push('productCodeTo=');
                search.push(paramProductPurchase.productCodeTo);
                search.push('&');
            }
            if (paramProductPurchase.productRegisterDateTo) {
                search.push('productRegisterDateTo=');
                search.push(paramProductPurchase.productRegisterDateTo.getTime());
                search.push('&');
            }
            if (paramProductPurchase.productRegisterDateFrom) {
                search.push('productRegisterDateFrom=');
                search.push(paramProductPurchase.productRegisterDateFrom.getTime());
                search.push('&');
            }
            if (paramProductPurchase.productName) {
                search.push('productName=');
                search.push(paramProductPurchase.productName);
                search.push('&');
            }
            //Supplier Filters
            if (paramProductPurchase.supplierName) {
                search.push('supplierName=');
                search.push(paramProductPurchase.supplierName);
                search.push('&');
            }
            if (paramProductPurchase.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramProductPurchase.supplierMobile);
                search.push('&');
            }
            if (paramProductPurchase.supplierIdentityNumber) {
                search.push('supplierIdentityNumber=');
                search.push(paramProductPurchase.supplierIdentityNumber);
                search.push('&');
            }

            ProductPurchaseService.filter(search.join("")).then(function (data) {
                $scope.productPurchases = data.content;

                $scope.pageProductPurchase.currentPage = $scope.pageProductPurchase.page + 1;
                $scope.pageProductPurchase.first = data.first;
                $scope.pageProductPurchase.last = data.last;
                $scope.pageProductPurchase.number = data.number;
                $scope.pageProductPurchase.numberOfElements = data.numberOfElements;
                $scope.pageProductPurchase.size = data.size;
                $scope.pageProductPurchase.totalElements = data.totalElements;
                $scope.pageProductPurchase.totalPages = data.totalPages;
                $scope.pageProductPurchase.currentPageString = ($scope.pageProductPurchase.page + 1) + ' / ' + $scope.pageProductPurchase.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextProductPurchasesPage = function () {
            $scope.pageProductPurchase.page++;
            $scope.searchProductPurchases($scope.paramProductPurchase);
        };
        $scope.selectPrevProductPurchasesPage = function () {
            $scope.pageProductPurchase.page--;
            $scope.searchProductPurchases($scope.paramProductPurchase);
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Product                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.products = [];
        $scope.newProduct = function () {
            ModalProvider.openProductCreateModel().result.then(function (data) {
                $scope.products.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.parents = [];
        $scope.newParent = function () {
            ModalProvider.openParentCreateModel().result.then(function (data) {
                $scope.parents.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.productPurchases = [];
        $scope.newProductPurchase = function () {
            ModalProvider.openProductPurchaseCreateModel().result.then(function (data) {
                // $scope.productPurchases.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Contract                                                                                                   *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.contracts = [];
        $scope.paramContract = {};
        $scope.contracts.checkAll = false;

        $scope.pageContract = {};
        $scope.pageContract.sorts = [];
        $scope.pageContract.page = 0;
        $scope.pageContract.totalPages = 0;
        $scope.pageContract.currentPage = $scope.pageContract.page + 1;
        $scope.pageContract.currentPageString = ($scope.pageContract.page + 1) + ' / ' + $scope.pageContract.totalPages;
        $scope.pageContract.size = 25;
        $scope.pageContract.first = true;
        $scope.pageContract.last = true;

        $scope.openContractsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/billPurchase/contractFilter.html',
                controller: 'contractFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramContract) {
                $scope.searchContracts(paramContract);
            }, function () {
            });
        };
        $scope.searchContracts = function (paramContract) {
            var search = [];
            search.push('size=');
            search.push($scope.pageContract.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageContract.page);
            search.push('&');
            angular.forEach($scope.pageContract.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageContract.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            //Contract Filters
            if (paramContract.codeFrom) {
                search.push('codeFrom=');
                search.push(paramContract.codeFrom);
                search.push('&');
            }
            if (paramContract.codeTo) {
                search.push('codeTo=');
                search.push(paramContract.codeTo);
                search.push('&');
            }
            if (paramContract.dateTo) {
                search.push('dateTo=');
                search.push(paramContract.dateTo.getTime());
                search.push('&');
            }
            if (paramContract.dateFrom) {
                search.push('dateFrom=');
                search.push(paramContract.dateFrom.getTime());
                search.push('&');
            }
            //Customer Filters
            if (paramContract.customerCodeFrom) {
                search.push('customerCodeFrom=');
                search.push(paramContract.customerCodeFrom);
                search.push('&');
            }
            if (paramContract.customerCodeTo) {
                search.push('customerCodeTo=');
                search.push(paramContract.customerCodeTo);
                search.push('&');
            }
            if (paramContract.customerRegisterDateTo) {
                search.push('customerRegisterDateTo=');
                search.push(paramContract.customerRegisterDateTo.getTime());
                search.push('&');
            }
            if (paramContract.customerRegisterDateFrom) {
                search.push('customerRegisterDateFrom=');
                search.push(paramContract.customerRegisterDateFrom.getTime());
                search.push('&');
            }
            if (paramContract.customerName) {
                search.push('customerName=');
                search.push(paramContract.customerName.getTime());
                search.push('&');
            }
            if (paramContract.customerMobile) {
                search.push('customerMobile=');
                search.push(paramContract.customerMobile.getTime());
                search.push('&');
            }
            //Supplier Filters
            if (paramContract.supplierCodeFrom) {
                search.push('supplierCodeFrom=');
                search.push(paramContract.supplierCodeFrom);
                search.push('&');
            }
            if (paramContract.supplierCodeTo) {
                search.push('supplierCodeTo=');
                search.push(paramContract.supplierCodeTo);
                search.push('&');
            }
            if (paramContract.supplierRegisterDateTo) {
                search.push('supplierRegisterDateTo=');
                search.push(paramContract.supplierRegisterDateTo.getTime());
                search.push('&');
            }
            if (paramContract.supplierRegisterDateFrom) {
                search.push('supplierRegisterDateFrom=');
                search.push(paramContract.supplierRegisterDateFrom.getTime());
                search.push('&');
            }
            if (paramContract.supplierName) {
                search.push('supplierName=');
                search.push(paramContract.supplierName.getTime());
                search.push('&');
            }
            if (paramContract.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramContract.supplierMobile.getTime());
                search.push('&');
            }
            ContractService.filter(search.join("")).then(function (data) {
                $scope.contracts = data.content;

                $scope.pageContract.currentPage = $scope.pageContract.page + 1;
                $scope.pageContract.first = data.first;
                $scope.pageContract.last = data.last;
                $scope.pageContract.number = data.number;
                $scope.pageContract.numberOfElements = data.numberOfElements;
                $scope.pageContract.size = data.size;
                $scope.pageContract.totalElements = data.totalElements;
                $scope.pageContract.totalPages = data.totalPages;
                $scope.pageContract.currentPageString = ($scope.pageContract.page + 1) + ' / ' + $scope.pageContract.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextContractsPage = function () {
            $scope.pageContract.page++;
            $scope.searchContracts($scope.paramContract);
        };
        $scope.selectPrevContractsPage = function () {
            $scope.pageContract.page--;
            $scope.searchContracts($scope.paramContract);
        };
        $scope.checkAllContracts = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllContracts input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.contracts, function (contract) {
                contract.isSelected = $scope.contracts.checkAll;
            });
        };
        $scope.checkContract = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllContracts').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllContracts').MaterialCheckbox.uncheck();
                }
            }
        };
        $scope.newContract = function () {
            ModalProvider.openContractCreateModel().result.then(function (data) {
                $scope.contracts.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.newOldContract = function () {
            ModalProvider.openContractOldCreateModel().result.then(function (data) {
                $scope.contracts.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.deleteContract = function (contract) {
            ModalProvider.openConfirmModel("العقود", "delete", "هل تود حذف العقد فعلاً؟").result.then(function (value) {
                if (value) {
                    ContractService.remove(contract.id).then(function () {
                        var index = $scope.contracts.indexOf(contract);
                        $scope.contracts.splice(index, 1);
                    });
                }
            });
        };
        $scope.newContractProduct = function (contract) {
            ModalProvider.openContractProductCreateModel(contract).result.then(function (data) {
                if (!contract.contractProducts) {
                    contract.contractProducts = [];
                }
                return contract.contractProducts.splice(0, 0, data);
            });
        };
        $scope.newContractPremium = function (contract) {
            ModalProvider.openContractPremiumCreateModel(contract).result.then(function (data) {
                if (!contract.contractPremiums) {
                    contract.contractPremiums = [];
                }
                return contract.contractPremiums.splice(0, 0, data);
            });
        };
        $scope.newContractPayment = function (contract) {
            ModalProvider.openContractPaymentCreateModel(contract).result.then(function (data) {
                if (!contract.contractPayments) {
                    contract.contractPayments = [];
                }
                return contract.contractPayments.splice(0, 0, data);
            });
        };
        $scope.rowMenuContract = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newContract();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteContract($itemScope.contract);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/product.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>اضافة سلعة...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_PRODUCT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newContractProduct($itemScope.contract);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/calendar.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>اضافة قسط...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_PREMIUM_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newContractPremium($itemScope.contract);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/bankTransaction.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تسديد دفعة مالية...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_PAYMENT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newContractPayment($itemScope.contract);
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
                    ModalProvider.openContractDetailsModel($itemScope.contract);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * ContractPremium                                                                                            *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.contractPremiums = [];
        $scope.paramContractPremium = {};
        $scope.contractPremiums.checkAll = false;

        $scope.pageContractPremium = {};
        $scope.pageContractPremium.sorts = [];
        $scope.pageContractPremium.page = 0;
        $scope.pageContractPremium.totalPages = 0;
        $scope.pageContractPremium.currentPage = $scope.pageContractPremium.page + 1;
        $scope.pageContractPremium.currentPageString = ($scope.pageContractPremium.page + 1) + ' / ' + $scope.pageContractPremium.totalPages;
        $scope.pageContractPremium.size = 25;
        $scope.pageContractPremium.first = true;
        $scope.pageContractPremium.last = true;

        $scope.openContractPremiumsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/contractPremium/contractPremiumFilter.html',
                controller: 'contractPremiumFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramContractPremium) {
                $scope.searchContractPremiums(paramContractPremium);
            }, function () {
            });
        };
        $scope.searchContractPremiums = function (paramContractPremium) {
            var search = [];
            search.push('size=');
            search.push($scope.pageContractPremium.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageContractPremium.page);
            search.push('&');
            angular.forEach($scope.pageContractPremium.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageContractPremium.sorts.length === 0) {
                search.push('sort=dueDate,desc&');
            }
            if (paramContractPremium.dueDateFrom) {
                search.push('dueDateFrom=');
                search.push(paramContractPremium.dueDateFrom.getTime());
                search.push('&');
            }
            if (paramContractPremium.dueDateTo) {
                search.push('dueDateTo=');
                search.push(paramContractPremium.dueDateTo.getTime());
                search.push('&');
            }
            if (paramContractPremium.contractCodeFrom) {
                search.push('contractCodeFrom=');
                search.push(paramContractPremium.contractCodeFrom);
                search.push('&');
            }
            if (paramContractPremium.contractCodeTo) {
                search.push('contractCodeTo=');
                search.push(paramContractPremium.contractCodeTo);
                search.push('&');
            }
            if (paramContractPremium.contractDateFrom) {
                search.push('contractDateFrom=');
                search.push(paramContractPremium.contractDateFrom.getTime());
                search.push('&');
            }
            if (paramContractPremium.contractDateTo) {
                search.push('contractDateTo=');
                search.push(paramContractPremium.contractDateTo.getTime());
                search.push('&');
            }
            if (paramContractPremium.customerName) {
                search.push('customerName=');
                search.push(paramContractPremium.customerName);
                search.push('&');
            }
            if (paramContractPremium.customerMobile) {
                search.push('customerMobile=');
                search.push(paramContractPremium.customerMobile);
                search.push('&');
            }
            if (paramContractPremium.supplierName) {
                search.push('supplierName=');
                search.push(paramContractPremium.supplierName);
                search.push('&');
            }
            if (paramContractPremium.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramContractPremium.supplierMobile);
                search.push('&');
            }

            search.push('filterCompareType=or');

            ContractPremiumService.filter(search.join("")).then(function (data) {
                $scope.contractPremiums = data.content;

                $scope.pageContractPremium.currentPage = $scope.pageContractPremium.page + 1;
                $scope.pageContractPremium.first = data.first;
                $scope.pageContractPremium.last = data.last;
                $scope.pageContractPremium.number = data.number;
                $scope.pageContractPremium.numberOfElements = data.numberOfElements;
                $scope.pageContractPremium.size = data.size;
                $scope.pageContractPremium.totalElements = data.totalElements;
                $scope.pageContractPremium.totalPages = data.totalPages;
                $scope.pageContractPremium.currentPageString = ($scope.pageContractPremium.page + 1) + ' / ' + $scope.pageContractPremium.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextContractPremiumsPage = function () {
            $scope.pageContractPremium.page++;
            $scope.searchContractPremiums($scope.paramContractPremium);
        };
        $scope.selectPrevContractPremiumsPage = function () {
            $scope.pageContractPremium.page--;
            $scope.searchContractPremiums($scope.paramContractPremium);
        };
        $scope.checkAllContractPremiums = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllContractPremiums input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.contractPremiums, function (contractPremium) {
                contractPremium.isSelected = $scope.contractPremiums.checkAll;
            });
        };
        $scope.checkContractPremium = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllContractPremiums').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllContractPremiums').MaterialCheckbox.uncheck();
                }
            }
        };
        $scope.rowMenuContractPremium = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_PREMIUM_CREATE']);
                },
                click: function ($itemScope, $event, value) {

                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_CONTRACT_PREMIUM_DELETE']);
                },
                click: function ($itemScope, $event, value) {

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
                    var contractPremiums = [];
                    angular.forEach($scope.contractPremiums, function (contractPremium) {
                        if (contractPremium.isSelected) {
                            contractPremiums.push(contractPremium);
                        }
                    });
                    if (contractPremiums.length === 0) {
                        ModalProvider.openConfirmModel('إرسال رسائل الأقساط', 'send', 'فضلا قم باختيار قسط واحد على الأقل');
                        return;
                    }
                    ModalProvider.openContractPremiumSendMessageModel(contractPremiums);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * ContractPayment                                                                                            *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.contractPayments = [];
        $scope.paramContractPayment = {};
        $scope.contractPayments.checkAll = false;

        $scope.pageContractPayment = {};
        $scope.pageContractPayment.sorts = [];
        $scope.pageContractPayment.page = 0;
        $scope.pageContractPayment.totalPages = 0;
        $scope.pageContractPayment.currentPage = $scope.pageContractPayment.page + 1;
        $scope.pageContractPayment.currentPageString = ($scope.pageContractPayment.page + 1) + ' / ' + $scope.pageContractPayment.totalPages;
        $scope.pageContractPayment.size = 25;
        $scope.pageContractPayment.first = true;
        $scope.pageContractPayment.last = true;

        $scope.openContractPaymentsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/contractPayment/contractPaymentFilter.html',
                controller: 'contractPaymentFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramContractPayment) {
                $scope.searchContractPayments(paramContractPayment);
            }, function () {
            });
        };
        $scope.searchContractPayments = function (paramContractPayment) {
            var search = [];
            search.push('size=');
            search.push($scope.pageContractPayment.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageContractPayment.page);
            search.push('&');
            angular.forEach($scope.pageContractPayment.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageContractPayment.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            if (paramContractPayment.dateFrom) {
                search.push('dateFrom=');
                search.push(paramContractPayment.dateFrom.getTime());
                search.push('&');
            }
            if (paramContractPayment.dateTo) {
                search.push('dateTo=');
                search.push(paramContractPayment.dateTo.getTime());
                search.push('&');
            }
            if (paramContractPayment.contractCodeFrom) {
                search.push('contractCodeFrom=');
                search.push(paramContractPayment.contractCodeFrom);
                search.push('&');
            }
            if (paramContractPayment.contractCodeTo) {
                search.push('contractCodeTo=');
                search.push(paramContractPayment.contractCodeTo);
                search.push('&');
            }
            if (paramContractPayment.contractDateFrom) {
                search.push('contractDateFrom=');
                search.push(paramContractPayment.contractDateFrom.getTime());
                search.push('&');
            }
            if (paramContractPayment.contractDateTo) {
                search.push('contractDateTo=');
                search.push(paramContractPayment.contractDateTo.getTime());
                search.push('&');
            }
            if (paramContractPayment.customerName) {
                search.push('customerName=');
                search.push(paramContractPayment.customerName);
                search.push('&');
            }
            if (paramContractPayment.customerMobile) {
                search.push('customerMobile=');
                search.push(paramContractPayment.customerMobile);
                search.push('&');
            }
            if (paramContractPayment.supplierName) {
                search.push('supplierName=');
                search.push(paramContractPayment.supplierName);
                search.push('&');
            }
            if (paramContractPayment.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramContractPayment.supplierMobile);
                search.push('&');
            }

            search.push('filterCompareType=or');

            ContractPaymentService.filter(search.join("")).then(function (data) {
                $scope.contractPayments = data.content;

                $scope.pageContractPayment.currentPage = $scope.pageContractPayment.page + 1;
                $scope.pageContractPayment.first = data.first;
                $scope.pageContractPayment.last = data.last;
                $scope.pageContractPayment.number = data.number;
                $scope.pageContractPayment.numberOfElements = data.numberOfElements;
                $scope.pageContractPayment.size = data.size;
                $scope.pageContractPayment.totalElements = data.totalElements;
                $scope.pageContractPayment.totalPages = data.totalPages;
                $scope.pageContractPayment.currentPageString = ($scope.pageContractPayment.page + 1) + ' / ' + $scope.pageContractPayment.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextContractPaymentsPage = function () {
            $scope.pageContractPayment.page++;
            $scope.searchContractPayments($scope.paramContractPayment);
        };
        $scope.selectPrevContractPaymentsPage = function () {
            $scope.pageContractPayment.page--;
            $scope.searchContractPayments($scope.paramContractPayment);
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
        $scope.openReportContracts = function () {
            $scope.toggleReport = 'contracts';
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
        $scope.openReportContractPremiums = function () {
            $scope.toggleReport = 'contractPremiums';
            $rootScope.refreshGUI();
        };
        //تقرير أرباح التحصيل
        $scope.openReportContractPaymentsProfit = function () {
            $scope.toggleReport = 'contractPaymentsProfit';
            $rootScope.refreshGUI();
        };

        $scope.paramWithdrawCash = {};
        $scope.supplierStatement = {};
        $scope.customerStatement = {};
        $scope.contractPaymentProfit = {};
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
        $scope.fetchSupplierStatementContracts = function () {
            ContractService.findBySupplier($scope.supplierStatement.supplier.id).then(function (value) {
                $scope.supplierStatement.contracts = value;
            });
        };
        $scope.fetchSupplierStatementBanks = function () {
            BankService.findBySupplier($scope.supplierStatement.supplier.id).then(function (value) {
                $scope.supplierStatement.banks = value;
            });
        };
        $scope.fetchContractPaymentsProfit = function () {
            ContractPaymentService.findByDateBetween(
                $scope.contractPaymentProfit.dateFrom.getTime(),
                $scope.contractPaymentProfit.dateTo.getTime()
            )
                .then(function (value) {
                    $scope.contractPaymentProfit.contractPayments = value;
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