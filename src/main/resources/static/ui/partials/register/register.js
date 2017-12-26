function registerCtrl (
    PersonService,
    BranchService,
    MasterService,
    MasterCategoryService,
    OfferService,
    CourseService,
    AccountService,
    ModalProvider,
    $scope,
    $rootScope,
    $timeout,
    $uibModal) {

    /**************************************************************************************************************
     *                                                                                                            *
     * General                                                                                                    *
     *                                                                                                            *
     **************************************************************************************************************/
    $timeout(function () {
        PersonService.findAllCombo().then(function (data) {
            $scope.persons = data;
        });
        BranchService.fetchBranchMasterCourse().then(function (data) {
            $scope.branches = data;
        });
        $scope.refreshMasterCategory();
        window.componentHandler.upgradeAllRegistered();
    }, 1500);
    /**************************************************************************************************************
     *                                                                                                            *
     * Master                                                                                                     *
     *                                                                                                            *
     **************************************************************************************************************/
    //
    $scope.itemsMaster = [];
    $scope.itemsMaster.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsMaster.push({'id': 2, 'type': 'title', 'name': 'التخصصات'});
    //
    $scope.selectedMaster = {};
    $scope.branches = [];
    $scope.masters = [];
    $scope.masterCategories = [];
    $scope.refreshMasterCategory = function () {
        MasterCategoryService.findAll().then(function (data) {
            $scope.masterCategories = data;
        });
    };
    $scope.setSelectedMaster = function (object) {
        if (object) {
            angular.forEach($scope.masters, function (master) {
                if (object.id == master.id) {
                    $scope.selectedMaster = master;
                    return master.isSelected = true;
                } else {
                    return master.isSelected = false;
                }
            });
        }
    };
    $scope.newMaster = function () {
        ModalProvider.openMasterCreateModel().result.then(function (data) {
            $scope.masters.splice(0, 0, data);
        }, function () {
            console.info('MasterCreateModel Closed.');
        });
    };
    $scope.newMasterCategory = function () {
        ModalProvider.openMasterCategoryCreateModel().result.then(function (data) {
            $scope.masterCategories.splice(0, 0, data);
        }, function () {
            console.info('MasterCategoryCreateModel Closed.');
        });
    };
    $scope.deleteMasterCategory = function (masterCategory) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التصنيف فعلاً؟", "error", "fa-ban", function () {
            MasterCategoryService.remove(masterCategory.id).then(function () {
                var index = $scope.masterCategories.indexOf(masterCategory);
                $scope.masterCategories.splice(index, 1);
            });
        });
    };
    $scope.deleteMaster = function (master) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التخصص فعلاً؟", "error", "fa-ban", function () {
            MasterService.remove(master.id).then(function () {
                var index = $scope.masters.indexOf(master);
                $scope.masters.splice(index, 1);
                $scope.setSelectedMaster($scope.masters[0]);
            });
        });
    };
    $scope.filterMaster = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/master/masterFilter.html',
            controller: 'masterFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى التخصصات';
                }
            }
        });

        modalInstance.result.then(function (buffer) {
            var search = [];
            if (buffer.name) {
                search.push('name=');
                search.push(buffer.name);
                search.push('&');
            }
            if (buffer.codeFrom) {
                search.push('codeFrom=');
                search.push(buffer.codeFrom);
                search.push('&');
            }
            if (buffer.codeTo) {
                search.push('codeTo=');
                search.push(buffer.codeTo);
                search.push('&');
            }
            if (buffer.branch) {
                search.push('branchId=');
                search.push(buffer.branch.id);
                search.push('&');
            }

            MasterService.filter(search.join("")).then(function (data) {
                $scope.masters = data;
                $scope.setSelectedMaster(data[0]);
                $scope.itemsMaster = [];
                $scope.itemsMaster.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.itemsMaster.push({'id': 2, 'type': 'title', 'name': 'التخصصات', 'style': 'font-weight:bold'});
                $scope.itemsMaster.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsMaster.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                });
            });

        }, function () {
            console.info('MasterFilterModel Closed.');
        });
    };
    $scope.rowMenuMaster = [
        {
            html: '<div class="drop-menu"> انشاء تخصص جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openMasterCreateModel();
            }
        },
        {
            html: '<div class="drop-menu"> تعديل بيانات التخصص <span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openMasterUpdateModel($itemScope.master);
            }
        },
        {
            html: '<div class="drop-menu"> حذف التخصص <span class="fa fa-minus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteMaster($itemScope.master);
            }
        }
    ];
    /**************************************************************************************************************
     *                                                                                                            *
     * Offer                                                                                                      *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.offers = [];
    $scope.selectedOffer = {};
    //
    $scope.itemsOffer = [];
    $scope.itemsOffer.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsOffer.push({'id': 2, 'type': 'title', 'name': 'العروض'});
    //
    $scope.setSelectedOffer = function (object) {
        if (object) {
            angular.forEach($scope.offers, function (offer) {
                if (object.id == offer.id) {
                    $scope.selectedOffer = offer;
                    return offer.isSelected = true;
                } else {
                    return offer.isSelected = false;
                }
            });
        }
    };
    $scope.filterOffer = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/offer/offerFilter.html',
            controller: 'offerFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى العروض';
                }
            }
        });

        modalInstance.result.then(function (buffer) {
            var search = [];
            if (buffer.codeFrom) {
                search.push('codeFrom=');
                search.push(buffer.codeFrom);
                search.push('&');
            }
            if (buffer.codeTo) {
                search.push('codeTo=');
                search.push(buffer.codeTo);
                search.push('&');
            }
            if (buffer.dateFrom) {
                search.push('dateFrom=');
                search.push(buffer.dateFrom.getTime());
                search.push('&');
            }
            if (buffer.dateTo) {
                search.push('dateTo=');
                search.push(buffer.dateTo.getTime());
                search.push('&');
            }
            if (buffer.customerName) {
                search.push('customerName=');
                search.push(buffer.customerName);
                search.push('&');
            }
            if (buffer.customerIdentityNumber) {
                search.push('customerIdentityNumber=');
                search.push(buffer.customerIdentityNumber);
                search.push('&');
            }
            if (buffer.customerMobile) {
                search.push('customerMobile=');
                search.push(buffer.customerMobile);
                search.push('&');
            }
            if (buffer.masterPriceFrom) {
                search.push('masterPriceFrom=');
                search.push(buffer.masterPriceFrom);
                search.push('&');
            }
            if (buffer.masterPriceTo) {
                search.push('masterPriceTo=');
                search.push(buffer.masterPriceTo);
                search.push('&');
            }
            if (buffer.branch) {
                search.push('branch=');
                search.push(buffer.branch.id);
                search.push('&');
            }
            if (buffer.master) {
                search.push('master=');
                search.push(buffer.master.id);
                search.push('&');
            }
            if (buffer.person) {
                search.push('personId=');
                search.push(buffer.person.id);
                search.push('&');
            }

            OfferService.filter(search.join("")).then(function (data) {
                $scope.offers = data;
                $scope.setSelectedOffer(data[0]);
                $scope.itemsOffer = [];
                $scope.itemsOffer.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.itemsOffer.push({'id': 2, 'type': 'title', 'name': 'العروض', 'style': 'font-weight:bold'});
                $scope.itemsOffer.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsOffer.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                });
                if (buffer.master) {
                    $scope.itemsOffer.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.itemsOffer.push({
                        'id': 6,
                        'type': 'title',
                        'name': ' [ ' + buffer.master.code + ' ] ' + buffer.master.name
                    });
                }
            });

        }, function () {
            console.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.clear = function () {
        $scope.buffer = {};
        $scope.buffer.branch = $scope.branches[0];
    };
    $scope.newOffer = function () {
        ModalProvider.openOfferCreateModel().result.then(function (data) {
            $rootScope.showConfirmNotify("العروض", "هل تود طباعة العرض ؟", "notification", "fa-info", function () {
                $scope.printOffer(data);
            });
            $scope.offers.splice(0, 0, data);
        });
    };
    $scope.copyOffer = function (offer) {
        ModalProvider.openOfferCopyModel(offer).result.then(function (data) {
            $rootScope.showConfirmNotify("العروض", "هل تود طباعة العرض ؟", "notification", "fa-info", function () {
                $scope.printOffer(data);
            });
            $scope.offers.splice(0, 0, data);
        });
    };
    $scope.printOffer = function (offer) {
        window.open('/report/OfferById/' + offer.id + '?exportType=PDF');
    };
    $scope.deleteOffer = function (offer) {
        if (offer) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف العرض فعلاً؟", "error", "fa-ban", function () {
                OfferService.remove(offer.id).then(function () {
                    var index = $scope.offers.indexOf(offer);
                    $scope.offers.splice(index, 1);
                    $scope.setSelectedOffer($scope.offers[0]);
                });
            });
            return;
        }
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف العرض فعلاً؟", "error", "fa-ban", function () {
            OfferService.remove($scope.selectedOffer.id).then(function () {
                var index = $scope.offers.indexOf($scope.selectedOffer);
                $scope.offers.splice(index, 1);
                $scope.setSelectedOffer($scope.offers[0]);
            });
        });
    };
    $scope.callOffer = function (offer) {
        ModalProvider.openCallCreateModel(offer).result.then(function (call) {
            return offer.calls.push(call);
        }, function () {
            console.info('CallCreateModel Closed.');
        });
    };
    $scope.refreshCallsByOffer = function () {
        CallService.findByOffer($scope.selectedOffer.id).then(function (data) {
            $scope.selectedOffer.calls = data;
        });
    };
    $scope.rowMenuOffer = [
        {
            html: '<div class="drop-menu"> انشاء عرض جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.newOffer();
            }
        },
        {
            html: '<div class="drop-menu"> نسخ العرض <span class="fa fa-copy fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.copyOffer($itemScope.offer);
            }
        },
        {
            html: '<div class="drop-menu"> تعديل بيانات العرض <span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openOfferUpdateModel($itemScope.offer);
            }
        },
        {
            html: '<div class="drop-menu"> حذف العرض <span class="fa fa-minus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteOffer($itemScope.offer);
            }
        },
        {
            html: '<div class="drop-menu"> طباعة العرض <span class="fa fa-print fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.printOffer($itemScope.offer);
            }
        },
        {
            html: '<div class="drop-menu"> التفاصيل <span class="fa fa-info fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openOfferDetailsModel($itemScope.offer);
            }
        },
        {
            html: '<div class="drop-menu"> اجراء اتصال <span class="fa fa-phone fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.callOffer($itemScope.offer);
            }
        }
    ];
    /**************************************************************************************************************
     *                                                                                                            *
     * Course                                                                                                     *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.selectedCourse = {};
    $scope.courses = [];
    //
    $scope.itemsCourse = [];
    $scope.itemsCourse.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsCourse.push({'id': 2, 'type': 'title', 'name': 'الدورات'});
    //
    $scope.setSelectedCourse = function (object) {
        if (object) {
            angular.forEach($scope.courses, function (course) {
                if (object.id == course.id) {
                    $scope.selectedCourse = course;
                    return course.isSelected = true;
                } else {
                    return course.isSelected = false;
                }
            });
        }
    };
    $scope.newCourse = function () {
        ModalProvider.openCourseCreateModel().result.then(function (data) {
            $scope.courses.splice(0,0,data);
        }, function () {
            console.info('CourseCreateModel Closed.');
        });
    };
    $scope.deleteCourse = function (course) {
        if (course) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الدورة فعلاً؟", "error", "fa-ban", function () {
                CourseService.remove(course.id).then(function () {
                    var index = $scope.courses.indexOf(course);
                    $scope.courses.splice(index, 1);
                    $scope.setSelectedCourse($scope.courses[0]);
                });
            });
            return;
        }
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الدورة فعلاً؟", "error", "fa-ban", function () {
            CourseService.remove($scope.selectedCourse.id).then(function () {
                var index = $scope.courses.indexOf($scope.selectedCourse);
                $scope.courses.splice(index, 1);
                $scope.setSelectedCourse($scope.courses[0]);
            });
        });
    };
    $scope.deleteAccounts = function (course) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف طلاب الدورة فعلاً؟", "error", "fa-ban", function () {
            AccountService.removeByCourse(course.id).then(function () {
            });
        });
    };
    $scope.deletePayments = function (course) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف جميع سندات طلاب الدورة فعلاً؟", "error", "fa-ban", function () {
            PaymentService.removeByCourse(course.id).then(function () {

            });
        });
    };
    $scope.filterCourse = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/course/courseFilter.html',
            controller: 'courseFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى الدورات';
                }
            }
        });

        modalInstance.result.then(function (buffer) {
            var search = [];
            if (buffer.instructor) {
                search.push('instructor=');
                search.push(buffer.instructor);
                search.push('&');
            }
            if (buffer.codeFrom) {
                search.push('codeFrom=');
                search.push(buffer.codeFrom);
                search.push('&');
            }
            if (buffer.codeTo) {
                search.push('codeTo=');
                search.push(buffer.codeTo);
                search.push('&');
            }
            if (buffer.branch) {
                search.push('branchId=');
                search.push(buffer.branch.id);
                search.push('&');
            }
            if (buffer.master) {
                search.push('masterId=');
                search.push(buffer.master.id);
                search.push('&');
            }

            CourseService.filter(search.join("")).then(function (data) {
                $scope.courses = data;
                $scope.setSelectedCourse(data[0]);
                $scope.itemsCourse = [];
                $scope.itemsCourse.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.itemsCourse.push({'id': 2, 'type': 'title', 'name': 'الدورات', 'style': 'font-weight:bold'});
                $scope.itemsCourse.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsCourse.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                });
            });

        }, function () {
            console.info('CourseFilterModel Closed.');
        });
    };
    $scope.rowMenuCourse = [
        {
            html: '<div class="drop-menu">انشاء دورة جديد<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.newCourse();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات الدورة<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openCourseUpdateModel($itemScope.course);
            }
        },
        {
            html: '<div class="drop-menu">حذف الدورة<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.delete($itemScope.course);
            }
        },
        {
            html: '<div class="drop-menu">حذف طلاب الدورة<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteAccounts($itemScope.course);
            }
        },
        {
            html: '<div class="drop-menu">حذف جميع سندات طلاب الدورة<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deletePayments($itemScope.course);
            }
        }
    ];
    /**************************************************************************************************************
     *                                                                                                            *
     * Account                                                                                                    *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.account = {};
    $scope.accounts = [];
    //
    $scope.itemsAccount = [];
    $scope.itemsAccount.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsAccount.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب'});
    //
    $scope.setSelectedAccount = function (object) {
        if (object) {
            angular.forEach($scope.accounts, function (account) {
                if (object.id == account.id) {
                    $scope.selected = account;
                    return account.isSelected = true;
                } else {
                    return account.isSelected = false;
                }
            });
        }
    };
    $scope.newAccount = function () {
        ModalProvider.openAccountCreateModel().result.then(function (data) {
            $rootScope.showConfirmNotify("العروض", "هل تود طباعة العقد ؟", "notification", "fa-info", function () {
                $scope.printAccount(data);
            });
            $scope.accounts.splice(0,0,data);
        }, function () {
            console.info('CourseCreateModel Closed.');
        });
    };
    $scope.createFastAccount = function () {
        AccountService.create($scope.account).then(function (data) {
            AccountService.findOne(data.id).then(function (data) {
                $scope.accounts.splice(0, 0, data);
                $scope.account = {};
                $scope.form1.$setPristine();
            });
        });
    };
    $scope.newPayment = function () {
        ModalProvider.openAccountPaymentModel($scope.selected).result.then(function (data) {
            AccountService.findOne($scope.selected.id).then(function (data) {
                var index = $scope.accounts.indexOf($scope.selected);
                $scope.accounts[index] = data;
                $scope.setSelectedAccount(data);
            });
        });
    };
    $scope.printAccount = function (account) {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/account/accountContract.html',
            controller: 'accountContractCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                account: function () {
                    return account;
                }
            }
        });

        modalInstance.result.then(function (buffer) {

        }, function () {
            console.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.searchAccount = function () {

        var search = [];
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
        if ($scope.buffer.dateFrom) {
            search.push('dateFrom=');
            search.push(moment($scope.buffer.dateFrom).valueOf());
            search.push('&');
        }
        if ($scope.buffer.dateTo) {
            search.push('dateTo=');
            search.push(moment($scope.buffer.dateTo).valueOf());
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
        if ($scope.buffer.coursePriceFrom) {
            search.push('coursePriceFrom=');
            search.push($scope.buffer.coursePriceFrom);
            search.push('&');
        }
        if ($scope.buffer.coursePriceTo) {
            search.push('coursePriceTo=');
            search.push($scope.buffer.coursePriceTo);
            search.push('&');
        }
        if ($scope.buffer.branch) {
            search.push('branchIds=');
            var branchIds = [];
            branchIds.push($scope.buffer.branch.id);
            search.push(branchIds);
            search.push('&');
        }
        if ($scope.buffer.master) {
            search.push('masterIds=');
            var masterIds = [];
            masterIds.push($scope.buffer.master.id);
            search.push(masterIds);
            search.push('&');
        }
        if ($scope.buffer.course) {
            search.push('courseIds=');
            var courseIds = [];
            courseIds.push($scope.buffer.course.id);
            search.push(courseIds);
            search.push('&');
        }
        AccountService.filterWithInfo(search.join("")).then(function (data) {
            $scope.accounts = data;
            $scope.setSelectedAccount(data[0]);
            $scope.itemsAccount = [];
            $scope.itemsAccount.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
            $scope.itemsAccount.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب', 'style': 'font-weight:bold'});
            $scope.itemsAccount.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
            $scope.itemsAccount.push({
                'id': 4,
                'type': 'title',
                'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
            });
            if ($scope.buffer.master) {
                $scope.itemsAccount.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                $scope.itemsAccount.push({
                    'id': 6,
                    'type': 'title',
                    'name': ' [ ' + $scope.buffer.master.code + ' ] ' + $scope.buffer.master.name
                });
            }
            if ($scope.buffer.course) {
                $scope.itemsAccount.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                $scope.itemsAccount.push({
                    'id': 8,
                    'type': 'title',
                    'name': ' [ ' + $scope.buffer.course.code + ' ] '
                });
            }

            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 600);

        });

    };

    $scope.filterAccount = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/account/accountFilter.html',
            controller: 'accountFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى التسجيل';
                }
            }
        });

        modalInstance.result.then(function (buffer) {

            $scope.buffer = buffer;

            $scope.searchAccount();

        }, function () {
            console.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.deleteAccount = function (account) {
        if (account) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
                AccountService.remove(account.id).then(function () {
                    var index = $scope.accounts.indexOf(account);
                    $scope.accounts.splice(index, 1);
                    $scope.setSelectedAccount($scope.accounts[0]);
                });
            });
            return;
        }
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
            AccountService.remove($scope.selected.id).then(function () {
                var index = $scope.accounts.indexOf($scope.selected);
                $scope.accounts.splice(index, 1);
                $scope.setSelectedAccount($scope.accounts[0]);
            });
        });
    };
    $scope.rowMenuAccount = [
        {
            html: '<div class="drop-menu">انشاء تسجيل جديد<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.newAccount();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات التسجيل<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openAccountUpdateModel($itemScope.account);
            }
        },
        {
            html: '<div class="drop-menu">حذف التسجيل<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteAccount($itemScope.account);
            }
        },
        {
            html: '<div class="drop-menu">سند قبض<span class="fa fa-money fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.newPayment($itemScope.account);
            }
        },
        {
            html: '<div class="drop-menu"> التفاصيل <span class="fa fa-info fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openAccountDetailsModel($itemScope.account);
            }
        },
        {
            html: '<div class="drop-menu">طباعة عقد<span class="fa fa-print fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.printAccount($itemScope.account);
            }
        }
    ];


};

registerCtrl.$inject = [
    'PersonService',
    'BranchService',
    'MasterService',
    'MasterCategoryService',
    'OfferService',
    'CourseService',
    'AccountService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$timeout',
    '$uibModal'];

app.controller("registerCtrl", registerCtrl);