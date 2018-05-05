app.controller("menuCtrl", [
    'CompanyService',
    'AttachTypeService',
    'PersonService',
    'TeamService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$state',
    '$uibModal',
    '$timeout',
    function (CompanyService,
              AttachTypeService,
              PersonService,
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
                    $scope.pageTitle = 'الشركة';
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
        $scope.openStateTeam = function () {
            $scope.toggleState = 'team';
            $rootScope.refreshGUI();
        };
        $scope.openStatePerson = function () {
            $scope.toggleState = 'person';
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
            $rootScope.refreshGUI();
        };
        $scope.menuOptionsBody = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/admin.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>الإدارة</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE']);
                },
                click: function ($itemScope, $event, value) {

                },
                children: [
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/company.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>بيانات الشركة</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_COMPANY_UPDATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateCompany();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/person.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل المستخدمين</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE', 'ROLE_PERSON_UPDATE', 'ROLE_PERSON_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStatePerson();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>مستخدم جديد</span>' +
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
                        '<img src="/ui/img/' + $rootScope.iconSet + '/team.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل الصلاحيات</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE', 'ROLE_TEAM_UPDATE', 'ROLE_TEAM_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateTeam();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>صلاحيات جديدة</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newTeam();
                        }
                    }
                ]
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Company                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.selectedCompany = {};
        $scope.submitCompany = function () {
            CompanyService.update($scope.selectedCompany).then(function (data) {
                $scope.selectedCompany = data;
            });
        };
        $scope.browseCompanyLogo = function () {
            document.getElementById('uploader-company').click();
        };
        $scope.uploadCompanyLogo = function (files) {
            CompanyService.uploadCompanyLogo(files[0]).then(function (data) {
                $scope.selectedCompany.logo = data;
                CompanyService.update($scope.selectedCompany).then(function (data) {
                    $scope.selectedCompany = data;
                });
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Person                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
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
            ModalProvider.openConfirmModel("حذف المستخدمين", "delete", "هل تود حذف المستخدم فعلاً؟").result.then(function (value) {
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
            ModalProvider.openConfirmModel("حذف مجموعات الصلاحيات", "delete", "هل تود حذف المجموعة فعلاً؟").result.then(function (value) {
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
        $scope.printToCart = function (printSectionId) {
            var innerContents = document.getElementById(printSectionId).innerHTML;
            var popupWindow = window.open('', '_blank', 'width=600,height=700,scrollbars=no,menubar=no,toolbar=no,location=no,status=no,titlebar=no');
            popupWindow.document.open();
            popupWindow.document.write('' +
                '<html>' +
                '<head>' +
                '<link rel="stylesheet" type="text/css" href="/ui/app.css" />' +
                '<link rel="stylesheet" type="text/css" href="/ui/css/style.css" />' +
                '</head>' +
                '<body onload="window.print()">' + innerContents + '' +
                '</html>'
            );
            popupWindow.document.close();
        };

        $timeout(function () {
            CompanyService.get().then(function (data) {
                $scope.selectedCompany = data;
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