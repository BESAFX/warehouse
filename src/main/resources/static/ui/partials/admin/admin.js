function adminCtrl (
    CompanyService,
    BranchService,
    PersonService,
    TeamService,
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
        CompanyService.get().then(function (data) {
            $scope.selectedCompany = data;
        });
        PersonService.findAllCombo().then(function (data) {
            $scope.personsCombo = data;
        });
        window.componentHandler.upgradeAllRegistered();
    }, 1500);
    /**************************************************************************************************************
     *                                                                                                            *
     * Company                                                                                                    *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.selectedCompany = {};
    $scope.submit = function () {
        CompanyService.update($scope.selectedCompany).then(function (data) {
            $scope.selectedCompany = data;
        });
    };
    $scope.uploadFile = function () {
        document.getElementById('uploader').click();
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
     * Branch                                                                                                     *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.selectedBranch = {};
    $scope.branches = [];
    $scope.fetchBranchTableData = function () {
        BranchService.fetchTableData().then(function (data) {
            $scope.branches = data;
            $scope.setSelectedBranch(data[0]);
        });
    };
    $scope.setSelectedBranch = function (object) {
        if (object) {
            angular.forEach($scope.branches, function (branch) {
                if (object.id == branch.id) {
                    $scope.selectedBranch = branch;
                    return branch.isSelected = true;
                } else {
                    return branch.isSelected = false;
                }
            });
        }
    };
    $scope.newBranch = function () {
        ModalProvider.openBranchCreateModel().result.then(function (data) {
            return $scope.branches.splice(0, 0, data);
        });
    };
    $scope.deleteBranch = function (branch) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفرع فعلاً؟", "error", "fa-ban", function () {
            BranchService.remove(branch.id).then(function () {
                var index = $scope.branches.indexOf(branch);
                $scope.branches.splice(index, 1);
                $scope.setSelectedBranch($scope.branches[0]);
            });
        });
    };
    $scope.rowMenuBranch = [
        {
            html: '<div class="drop-menu">انشاء فرع جديد<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.newBranch();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات الفرع<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_UPDATE']);
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openBranchUpdateModel($itemScope.branch);
            }
        },
        {
            html: '<div class="drop-menu">حذف الفرع<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_DELETE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteBranch($itemScope.branch);
            }
        }
    ];
    /**************************************************************************************************************
     *                                                                                                            *
     * Person                                                                                                     *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.selectedPerson = {};
    $scope.fetchPersonTableData = function () {
        PersonService.findAll().then(function (data) {
            $scope.persons = data;
            $scope.setSelectedPerson(data[0]);
        });
    };
    $scope.setSelectedPerson = function (object) {
        if (object) {
            angular.forEach($scope.persons, function (person) {
                if (object.id == person.id) {
                    $scope.selectedPerson = person;
                    return person.isSelected = true;
                } else {
                    return person.isSelected = false;
                }
            });
        }
    };
    $scope.newPerson = function () {
        ModalProvider.openPersonCreateModel().result.then(function (data) {
            return $scope.persons.splice(0, 0, data);
        });
    };
    $scope.deletePerson = function (person) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستخدم فعلاً؟", "error", "fa-ban", function () {
            PersonService.remove(person.id).then(function () {
                var index = $scope.persons.indexOf(person);
                $scope.persons.splice(index, 1);
                $scope.setSelectedPerson($scope.persons[0]);
            });
        });
    };
    $scope.rowMenuPerson = [
        {
            html: '<div class="drop-menu">انشاء مستخدم جديد<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.newPerson();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات المستخدم<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_UPDATE']);
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openPersonUpdateModel($itemScope.person);
            }
        },
        {
            html: '<div class="drop-menu">حذف المستخدم<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_DELETE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.deletePerson($itemScope.person);
            }
        },
        {
            html: '<div class="drop-menu">طباعة تقرير مختصر<span class="fa fa-print fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openPersonsReportModel($scope.persons);
            }
        }
    ];
    /**************************************************************************************************************
     *                                                                                                            *
     * Team                                                                                                       *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.selectedTeam = {};
    $scope.fetchTeamTableData = function () {
        TeamService.findAll().then(function (data) {
            $scope.teams = data;
            $scope.setSelectedTeam(data[0]);
        });
    };
    $scope.setSelectedTeam = function (object) {
        if (object) {
            angular.forEach($scope.teams, function (team) {
                if (object.id == team.id) {
                    $scope.selectedTeam = team;
                    return team.isSelected = true;
                } else {
                    return team.isSelected = false;
                }
            });
        }
    };
    $scope.newTeam = function () {
        ModalProvider.openTeamCreateModel().result.then(function (data) {
            return $scope.teams.splice(0, 0, data);
        });
    };
    $scope.deleteTeam = function (team) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المجموعة فعلاً؟", "error", "fa-trash", function () {
            TeamService.remove(team.id).then(function () {
                var index = $scope.teams.indexOf(team);
                $scope.teams.splice(index, 1);
                $scope.setSelectedTeam($scope.teams[0]);
            });
        });
    };
    $scope.rowMenuTeam = [
        {
            html: '<div class="drop-menu">انشاء مجموعة جديدة<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.newTeam();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات المجموعة<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_UPDATE']);
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openTeamUpdateModel($itemScope.team);
            }
        },
        {
            html: '<div class="drop-menu">حذف المجموعة<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_DELETE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteTeam($itemScope.team);
            }
        }
    ];
};

adminCtrl.$inject = [
    'CompanyService' ,
    'BranchService',
    'PersonService',
    'TeamService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$timeout',
    '$uibModal'];

app.controller("adminCtrl", adminCtrl);