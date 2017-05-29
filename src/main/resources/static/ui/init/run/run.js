app.run(['$http', '$window', 'PersonService', '$rootScope', '$log', '$stomp', 'defaultErrorMessageResolver', 'ModalProvider', 'ReportModelProvider', '$state', 'FileService',
    function ($http, $window, PersonService, $rootScope, $log, $stomp, defaultErrorMessageResolver, ModalProvider, ReportModelProvider, $state, FileService) {

        defaultErrorMessageResolver.getErrorMessages().then(function (errorMessages) {
            errorMessages['fieldRequired'] = 'هذا الحقل مطلوب';
        });

        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {
            $.noty.clearQueue(); // Clears the notification queue
            $.noty.closeAll(); // Close all notifications
            switch (toState.name) {
                case 'home': {
                    $rootScope.pageTitle = 'الرئيسية';
                    $rootScope.MDLIcon = 'dashboard';
                    break;
                }
                case 'menu': {
                    $rootScope.pageTitle = 'البرامج';
                    $rootScope.MDLIcon = 'widgets';
                    break;
                }
                case 'company': {
                    $rootScope.pageTitle = 'الشركات';
                    $rootScope.MDLIcon = 'account_balance';
                    break;
                }
                case 'branch': {
                    $rootScope.pageTitle = 'الفروع';
                    $rootScope.MDLIcon = 'layers';
                    break;
                }
                case 'master': {
                    $rootScope.pageTitle = 'التخصصات';
                    $rootScope.MDLIcon = 'import_contacts';
                    break;
                }
                case 'offer': {
                    $rootScope.pageTitle = 'العروض';
                    $rootScope.MDLIcon = 'collections_bookmark';
                    break;
                }
                case 'course': {
                    $rootScope.pageTitle = 'الدورات';
                    $rootScope.MDLIcon = 'card_travel';
                    break;
                }
                case 'account': {
                    $rootScope.pageTitle = 'التسجيل';
                    $rootScope.MDLIcon = 'local_library';
                    break;
                }
                case 'payment': {
                    $rootScope.pageTitle = 'سندات القبض';
                    $rootScope.MDLIcon = 'monetization_on';
                    break;
                }
                case 'paymentOut': {
                    $rootScope.pageTitle = 'سندات الصرف';
                    $rootScope.MDLIcon = 'money_off';
                    break;
                }
                case 'billBuyType': {
                    $rootScope.pageTitle = 'حسابات فواتير الشراء';
                    $rootScope.MDLIcon = 'subtitles';
                    break;
                }
                case 'billBuy': {
                    $rootScope.pageTitle = 'فواتير الشراء';
                    $rootScope.MDLIcon = 'shopping_cart';
                    break;
                }
                case 'bank': {
                    $rootScope.pageTitle = 'الحسابات البنكية';
                    $rootScope.MDLIcon = 'account_balance';
                    break;
                }
                case 'deposit': {
                    $rootScope.pageTitle = 'الإيداع البنكي';
                    $rootScope.MDLIcon = 'archive';
                    break;
                }
                case 'withdraw': {
                    $rootScope.pageTitle = 'السحب البنكي';
                    $rootScope.MDLIcon = 'unarchive';
                    break;
                }
                case 'team': {
                    $rootScope.pageTitle = 'مجموعة الصلاحيات';
                    $rootScope.MDLIcon = 'settings_input_composite';
                    break;
                }
                case 'person': {
                    $rootScope.pageTitle = 'حسابات المستخدمين';
                    $rootScope.MDLIcon = 'lock';
                    break;
                }
                case 'profile': {
                    $rootScope.pageTitle = 'الملف الشخصي';
                    $rootScope.MDLIcon = 'lock';
                    break;
                }
                case 'report': {
                    $rootScope.pageTitle = 'التقارير';
                    break;
                }
                case 'help': {
                    $rootScope.pageTitle = 'المساعدة';
                    break;
                }
            }
        });

        $rootScope.contains = function (list, values) {
            return _.intersection(values, list).length > 0;
        };

        $rootScope.logout = function () {
            $http.post('/logout');
            $window.location.href = '/logout';
        };

        $rootScope.ReportModelProvider = ReportModelProvider;

        $rootScope.ModalProvider = ModalProvider;

        PersonService.findAuthorities().then(function (data) {
            $rootScope.authorities = data;
            $rootScope.me = {};
            PersonService.findActivePerson().then(function (data) {
                $rootScope.me = data;
                FileService.getSharedLink($rootScope.me.photo).then(function (data) {
                    $rootScope.me.img = data;
                });
            });
        });

        $rootScope.showNotify = function (title, message, type, icon) {
            noty({
                layout: 'topLeft',
                theme: 'metroui', // or relax
                type: type, // success, error, warning, information, notification
                text: '<div class="activity-item text-right"><span>' + title + '</span> <i class="fa ' + icon + '"></i><div class="activity">' + message + '</div></div>',
                dismissQueue: true, // [boolean] If you want to use queue feature set this true
                force: true, // [boolean] adds notification to the beginning of queue when set to true
                maxVisible: 3, // [integer] you can set max visible notification count for dismissQueue true option,
                template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
                timeout: 1500, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
                progressBar: true, // [boolean] - displays a progress bar
                animation: {
                    open: 'animated fadeIn',
                    close: 'animated fadeOut',
                    easing: 'swing',
                    speed: 500 // opening & closing animation speed
                },
                closeWith: ['hover'], // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
                modal: false, // [boolean] if true adds an overlay
                killer: false, // [boolean] if true closes all notifications and shows itself
                callback: {
                    onShow: function () {
                    },
                    afterShow: function () {
                    },
                    onClose: function () {
                    },
                    afterClose: function () {
                    },
                    onCloseClick: function () {
                    },
                },
                buttons: false // [boolean|array] an array of buttons, for creating confirmation dialogs.
            });
        };

        $rootScope.showConfirmNotify = function (title, message, type, icon, callback) {
            noty({
                layout: 'center',
                theme: 'metroui', // or relax
                type: type, // success, error, warning, information, notification
                text: '<div class="activity-item text-right"><span>' + title + '</span> <i class="fa ' + icon + '"></i><div class="activity">' + message + '</div></div>',
                dismissQueue: true, // [boolean] If you want to use queue feature set this true
                force: false, // [boolean] adds notification to the beginning of queue when set to true
                maxVisible: 3, // [integer] you can set max visible notification count for dismissQueue true option,
                template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
                timeout: false, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
                progressBar: true, // [boolean] - displays a progress bar
                animation: {
                    open: 'animated zoomIn',
                    close: 'animated zoomOut',
                    easing: 'swing',
                    speed: 500 // opening & closing animation speed
                },
                closeWith: ['button'], // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
                modal: true, // [boolean] if true adds an overlay
                killer: false, // [boolean] if true closes all notifications and shows itself
                callback: {
                    onShow: function () {
                    },
                    afterShow: function () {
                    },
                    onClose: function () {
                    },
                    afterClose: function () {
                    },
                    onCloseClick: function () {
                    },
                },
                buttons: [
                    {
                        addClass: 'btn btn-primary', text: 'نعم', onClick: function ($noty) {
                        $noty.close();
                        callback();
                    }
                    },
                    {
                        addClass: 'btn btn-danger', text: 'إلغاء', onClick: function ($noty) {
                        $noty.close();
                    }
                    }
                ]
            });
        };

        $rootScope.showTechnicalNotify = function (title, message, type, icon) {
            noty({
                layout: 'center',
                theme: 'metroui', // or relax
                type: type, // success, error, warning, information, notification
                text: '<div class="activity-item text-right"><span>' + title + '</span> <i class="fa ' + icon + '"></i><div class="activity">' + message + '</div></div>',
                dismissQueue: true, // [boolean] If you want to use queue feature set this true
                force: false, // [boolean] adds notification to the beginning of queue when set to true
                maxVisible: 3, // [integer] you can set max visible notification count for dismissQueue true option,
                template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
                timeout: false, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
                progressBar: true, // [boolean] - displays a progress bar
                animation: {
                    open: 'animated tada',
                    close: 'animated bounceOutUp',
                    easing: 'swing',
                    speed: 500 // opening & closing animation speed
                },
                closeWith: ['button'], // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
                modal: true, // [boolean] if true adds an overlay
                killer: true, // [boolean] if true closes all notifications and shows itself
                buttons: [
                    {
                        addClass: 'btn btn-primary', text: 'إعادة تحميل الصفحة', onClick: function ($noty) {
                        $noty.close();
                        location.reload(true);
                    }
                    },
                    {
                        addClass: 'btn btn-danger', text: 'إغلاق', onClick: function ($noty) {
                        $noty.close();
                    }
                    }
                ]
            });
        };

        /**************************************************************
         *                                                            *
         * STOMP Connect                                              *
         *                                                            *
         *************************************************************/
        $rootScope.chats = [];
        $stomp.connect('/ws').then(function () {
            $stomp.subscribe('/user/queue/notify', function (payload, headers, res) {
                $rootScope.showNotify(payload.title, payload.message, payload.type, payload.icon);
            }, {'headers': 'notify'});
        });
        $rootScope.today = new Date();

        /**************************************************************
         *                                                            *
         * Navigation Callers                                         *
         *                                                            *
         *************************************************************/
        $rootScope.goToCompany = function () {
            $state.go('company');
        };
        $rootScope.goToBranch = function () {
            $state.go('branch');
        };
        $rootScope.goToMaster = function () {
            $state.go('master');
        };
        $rootScope.goToOffer = function () {
            $state.go('offer');
        };
        $rootScope.goToCourse = function () {
            $state.go('course');
        };
        $rootScope.goToAccount = function () {
            $state.go('account');
        };
        $rootScope.goToPayment = function () {
            $state.go('payment');
        };
        $rootScope.goToPaymentOut = function () {
            $state.go('paymentOut');
        };
        $rootScope.goToBillBuyType = function () {
            $state.go('billBuyType');
        };
        $rootScope.goToBillBuy = function () {
            $state.go('billBuy');
        };
        $rootScope.goToBank = function () {
            $state.go('bank');
        };
        $rootScope.goToDeposit = function () {
            $state.go('deposit');
        };
        $rootScope.goToWithdraw = function () {
            $state.go('withdraw');
        };
        $rootScope.goToTeam = function () {
            $state.go('team');
        };
        $rootScope.goToPerson = function () {
            $state.go('person');
        };
        $rootScope.goToHome = function () {
            $state.go('home');
        };
        $rootScope.goToHelp = function () {
            $state.go('help');
        };
        $rootScope.goToProfile = function () {
            $state.go('profile');
        };

    }]);