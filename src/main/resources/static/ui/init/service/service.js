app.service('ModalProvider', ['$uibModal', '$log', function ($uibModal, $log) {

    /**************************************************************
     *                                                            *
     * Customer Model                                             *
     *                                                            *
     *************************************************************/
    this.openCustomerCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/customer/customerCreateUpdate.html',
            controller: 'customerCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'عميل / كفيل جديد';
                },
                action: function () {
                    return 'create';
                },
                customer: function () {
                    return {};
                }
            }
        });
    };

    this.openCustomerUpdateModel = function (customer) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/customer/customerCreateUpdate.html',
            controller: 'customerCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات عميل / كفيل';
                },
                action: function () {
                    return 'update';
                },
                customer: function () {
                    return customer;
                }
            }
        });
    };

    this.openCustomerSendMessageModel = function (customers) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/customer/customerSendMessage.html',
            controller: "customerSendMessageCtrl",
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                customers: function () {
                    return customers;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Supplier Model                                               *
     *                                                            *
     *************************************************************/
    this.openSupplierCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/supplier/supplierCreateUpdate.html',
            controller: 'supplierCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'مورد جديد';
                },
                action: function () {
                    return 'create';
                },
                supplier: function () {
                    return {};
                }
            }
        });
    };

    this.openSupplierUpdateModel = function (supplier) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/supplier/supplierCreateUpdate.html',
            controller: 'supplierCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات مورد';
                },
                action: function () {
                    return 'update';
                },
                supplier: function () {
                    return supplier;
                }
            }
        });
    };

    this.openSupplierSendMessageModel = function (suppliers) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/supplier/supplierSendMessage.html',
            controller: "supplierSendMessageCtrl",
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                suppliers: function () {
                    return suppliers;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Product Model                                              *
     *                                                            *
     *************************************************************/
    this.openParentCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/product/parentCreateUpdate.html',
            controller: 'parentCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تصنيف رئيسي جديد';
                },
                action: function () {
                    return 'create';
                },
                product: function () {
                    return {};
                }
            }
        });
    };

    this.openParentUpdateModel = function (product) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/product/parentCreateUpdate.html',
            controller: 'parentCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات التصنيف الرئيسي';
                },
                action: function () {
                    return 'update';
                },
                product: function () {
                    return product;
                }
            }
        });
    };

    this.openProductCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/product/productCreateUpdate.html',
            controller: 'productCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تصنيف فرعي جديد';
                },
                action: function () {
                    return 'create';
                },
                product: function () {
                    return {};
                }
            }
        });
    };

    this.openProductUpdateModel = function (product) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/product/productCreateUpdate.html',
            controller: 'productCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات التصنيف الفرعي';
                },
                action: function () {
                    return 'update';
                },
                product: function () {
                    return product;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillPurchase Model                                         *
     *                                                            *
     *************************************************************/
    this.openBillPurchaseCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billPurchase/billPurchaseCreate.html',
            controller: 'billPurchaseCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            windowClass: 'xlg'
        });
    };

    this.openBillPurchaseOldCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billPurchase/billPurchaseOldCreate.html',
            controller: 'billPurchaseOldCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            windowClass: 'xlg'
        });
    };

    this.openBillPurchaseDetailsModel = function (billPurchase) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billPurchase/billPurchaseDetails.html',
            controller: 'billPurchaseDetailsCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                billPurchase: function () {
                    return billPurchase;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillPurchaseProduct Model                                  *
     *                                                            *
     *************************************************************/
    this.openBillPurchaseProductCreateModel = function (billPurchase) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billPurchaseProduct/billPurchaseProductCreate.html',
            controller: 'billPurchaseProductCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                billPurchase: function () {
                    return billPurchase;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillPurchasePayment Model                                  *
     *                                                            *
     *************************************************************/
    this.openBillPurchasePaymentCreateModel = function (billPurchase) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billPurchasePayment/billPurchasePaymentCreate.html',
            controller: 'billPurchasePaymentCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                billPurchase: function () {
                    return billPurchase;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillSell Model                                             *
     *                                                            *
     *************************************************************/
    this.openBillSellCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billSell/billSellCreate.html',
            controller: 'billSellCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            windowClass: 'xlg'
        });
    };

    this.openBillSellOldCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billSell/billSellOldCreate.html',
            controller: 'billSellOldCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            windowClass: 'xlg'
        });
    };

    this.openBillSellDetailsModel = function (billSell) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billSell/billSellDetails.html',
            controller: 'billSellDetailsCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                billSell: function () {
                    return billSell;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillSellProduct Model                                      *
     *                                                            *
     *************************************************************/
    this.openBillSellProductCreateModel = function (billSell) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billSellProduct/billSellProductCreate.html',
            controller: 'billSellProductCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                billSell: function () {
                    return billSell;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillSellPayment Model                                      *
     *                                                            *
     *************************************************************/
    this.openBillSellPaymentCreateModel = function (billSell) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billSellPayment/billSellPaymentCreate.html',
            controller: 'billSellPaymentCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                billSell: function () {
                    return billSell;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * DepositCreate Model                                        *
     *                                                            *
     *************************************************************/
    this.openDepositCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bankTransaction/depositCreate.html',
            controller: 'depositCreateCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    /**************************************************************
     *                                                            *
     * WithdrawCreate Model                                       *
     *                                                            *
     *************************************************************/
    this.openWithdrawCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bankTransaction/withdrawCreate.html',
            controller: 'withdrawCreateCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    /**************************************************************
     *                                                            *
     * TransferCreate Model                                       *
     *                                                            *
     *************************************************************/
    this.openTransferCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bankTransaction/transferCreate.html',
            controller: 'transferCreateCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    /**************************************************************
     *                                                            *
     * WithdrawCashCreate Model                                   *
     *                                                            *
     *************************************************************/
    this.openWithdrawCashCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bankTransaction/withdrawCashCreate.html',
            controller: 'withdrawCashCreateCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    /**************************************************************
     *                                                            *
     * Team Model                                                 *
     *                                                            *
     *************************************************************/
    this.openTeamCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/team/teamCreateUpdate.html',
            controller: 'teamCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'مجموعة جديدة';
                },
                action: function () {
                    return 'create';
                },
                team: function () {
                    return undefined;
                }
            }
        });
    };

    this.openTeamUpdateModel = function (team) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/team/teamCreateUpdate.html',
            controller: 'teamCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات مجموعة';
                },
                action: function () {
                    return 'update';
                },
                team: function () {
                    return team;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Person Model                                               *
     *                                                            *
     *************************************************************/
    this.openPersonCreateModel = function () {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/person/personCreateUpdate.html',
            controller: 'personCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'مستخدم جديد';
                },
                action: function () {
                    return 'create';
                },
                person: function () {
                    return {};
                }
            }
        });
    };

    this.openPersonUpdateModel = function (person) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/person/personCreateUpdate.html',
            controller: 'personCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات مستخدم';
                },
                action: function () {
                    return 'update';
                },
                person: function () {
                    return person;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Confirm Model                                              *
     *                                                            *
     *************************************************************/
    this.openConfirmModel = function (title, icon, message) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/modal/confirmModal.html',
            controller: 'confirmModalCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return title;
                },
                icon: function () {
                    return icon;
                },
                message: function () {
                    return message;
                }
            }
        });
    };

}]);

app.service('NotificationProvider', ['$http', function ($http) {

    this.notifyOne = function (code, title, message, type, receiver) {
        $http.post("/notifyOne?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
            + '&'
            + 'receiver=' + receiver);
    };
    this.notifyAll = function (code, title, message, type) {
        $http.post("/notifyAll?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
        );
    };
    this.notifyOthers = function (code, title, message, type) {
        $http.post("/notifyOthers?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
        );
    };

}]);

