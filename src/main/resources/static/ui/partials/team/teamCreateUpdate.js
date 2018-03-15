app.controller('teamCreateUpdateCtrl', ['TeamService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'team',
    function (TeamService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, team) {

        $scope.title = title;

        $scope.action = action;

        $scope.roles = [];

        //////////////////////////Company////////////////////////////////////////
        $scope.roles.push({
            name: 'تعديل بيانات الشركة',
            value: 'ROLE_COMPANY_UPDATE',
            selected: false,
            category: 'الإدارة'
        });
        //////////////////////////Branch////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء الفروع',
            value: 'ROLE_BRANCH_CREATE',
            selected: false,
            category: 'الإدارة'
        });
        $scope.roles.push({
            name: 'تعديل الفروع',
            value: 'ROLE_BRANCH_UPDATE',
            selected: false,
            category: 'الإدارة'
        });
        $scope.roles.push({
            name: 'حذف الفروع',
            value: 'ROLE_BRANCH_DELETE',
            selected: false,
            category: 'الإدارة'
        });
        //////////////////////////Master Category////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء تصنيفات التخصصات',
            value: 'ROLE_MASTER_CATEGORY_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل تصنيفات التخصصات',
            value: 'ROLE_MASTER_CATEGORY_UPDATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حذف تصنيفات التخصصات',
            value: 'ROLE_MASTER_CATEGORY_DELETE',
            selected: false,
            category: 'التسجيل'
        });
        //////////////////////////Master////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء التخصصات',
            value: 'ROLE_MASTER_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل التخصصات',
            value: 'ROLE_MASTER_UPDATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حذف التخصصات',
            value: 'ROLE_MASTER_DELETE',
            selected: false,
            category: 'التسجيل'
        });
        //////////////////////////Offer////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء العروض',
            value: 'ROLE_OFFER_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل العروض',
            value: 'ROLE_OFFER_UPDATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حذف العروض',
            value: 'ROLE_OFFER_DELETE',
            selected: false,
            category: 'التسجيل'
        });
        //////////////////////////Course////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء الدورات',
            value: 'ROLE_COURSE_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل الدورات',
            value: 'ROLE_COURSE_UPDATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حذف الدورات',
            value: 'ROLE_COURSE_DELETE',
            selected: false,
            category: 'التسجيل'
        });
        //////////////////////////Account////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء حسابات الطلاب',
            value: 'ROLE_ACCOUNT_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل حسابات الطلاب',
            value: 'ROLE_ACCOUNT_UPDATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حذف حسابات الطلاب',
            value: 'ROLE_ACCOUNT_DELETE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حالة الطالب',
            value: 'ROLE_ACCOUNT_CONDITION_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'ملاحظات الطالب',
            value: 'ROLE_ACCOUNT_NOTE_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل رسوم تسجيل الطالب',
            value: 'ROLE_ACCOUNT_FEES_UPDATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'اضافة مستندات طالب',
            value: 'ROLE_ACCOUNT_ATTACH_CREATE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل اسم مستند طالب',
            value: 'ROLE_ACCOUNT_ATTACH_SET_NAME',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'تعديل نوع مستند طالب',
            value: 'ROLE_ACCOUNT_ATTACH_SET_TYPE',
            selected: false,
            category: 'التسجيل'
        });
        $scope.roles.push({
            name: 'حذف مستندات طالب',
            value: 'ROLE_ACCOUNT_ATTACH_DELETE',
            selected: false,
            category: 'التسجيل'
        });
        //////////////////////////Payment In////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء سندات القبض',
            value: 'ROLE_PAYMENT_CREATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'تعديل سندات القبض',
            value: 'ROLE_PAYMENT_UPDATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'حذف سندات القبض',
            value: 'ROLE_PAYMENT_DELETE',
            selected: false,
            category: 'المالية'
        });
        //////////////////////////Payment Out////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء سندات الصرف',
            value: 'ROLE_PAYMENT_OUT_CREATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'تعديل سندات الصرف',
            value: 'ROLE_PAYMENT_OUT_UPDATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'حذف سندات الصرف',
            value: 'ROLE_PAYMENT_OUT_DELETE',
            selected: false,
            category: 'المالية'
        });
        //////////////////////////Bill Buy Type////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء حسابات فواتير الشراء',
            value: 'ROLE_BILL_BUY_TYPE_CREATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'تعديل حسابات فواتير الشراء',
            value: 'ROLE_BILL_BUY_TYPE_UPDATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'حذف حسابات فواتير الشراء',
            value: 'ROLE_BILL_BUY_TYPE_DELETE',
            selected: false,
            category: 'المالية'
        });
        //////////////////////////Bill Buy////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء فواتير الشراء',
            value: 'ROLE_BILL_BUY_CREATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'تعديل فواتير الشراء',
            value: 'ROLE_BILL_BUY_UPDATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'حذف فواتير الشراء',
            value: 'ROLE_BILL_BUY_DELETE',
            selected: false,
            category: 'المالية'
        });
        //////////////////////////Bank////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء الحسابات البنكية',
            value: 'ROLE_BANK_CREATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'تعديل الحسابات البنكية',
            value: 'ROLE_BANK_UPDATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'حذف الحسابات البنكية',
            value: 'ROLE_BANK_DELETE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'إيداع بنكي',
            value: 'ROLE_DEPOSIT_CREATE',
            selected: false,
            category: 'المالية'
        });
        $scope.roles.push({
            name: 'سحب بنكي',
            value: 'ROLE_WITHDRAW_CREATE',
            selected: false,
            category: 'المالية'
        });
        //////////////////////////Person//////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء حسابات المستخدمين',
            value: 'ROLE_PERSON_CREATE',
            selected: false,
            category: 'الإدارة'
        });
        $scope.roles.push({
            name: 'تعديل حسابات المستخدمين',
            value: 'ROLE_PERSON_UPDATE',
            selected: false,
            category: 'الإدارة'
        });
        $scope.roles.push({
            name: 'حذف حسابات المستخدمين',
            value: 'ROLE_PERSON_DELETE',
            selected: false,
            category: 'الإدارة'
        });
        //////////////////////////Team////////////////////////////////////////////
        $scope.roles.push({
            name: 'إنشاء الصلاحيات',
            value: 'ROLE_TEAM_CREATE',
            selected: false,
            category: 'الإدارة'
        });
        $scope.roles.push({
            name: 'تعديل الصلاحيات',
            value: 'ROLE_TEAM_UPDATE',
            selected: false,
            category: 'الإدارة'
        });
        $scope.roles.push({
            name: 'حذف الصلاحيات',
            value: 'ROLE_TEAM_DELETE',
            selected: false,
            category: 'الإدارة'
        });


        if (team) {
            $scope.team = team;
            if (typeof team.authorities === 'string') {
                $scope.team.authorities = team.authorities.split(',');
            }
            //
            angular.forEach($scope.team.authorities, function (auth) {
                angular.forEach($scope.roles, function (role) {
                    if (role.value === auth) {
                        return role.selected = true;
                    }
                })
            });
        } else {
            $scope.team = {};
        }

        $scope.submit = function () {
            $scope.team.authorities = [];
            angular.forEach($scope.roles, function (role) {
                if (role.selected) {
                    $scope.team.authorities.push(role.value);
                }
            });
            $scope.team.authorities = $scope.team.authorities.join();
            switch ($scope.action) {
                case 'create' :
                    TeamService.create($scope.team).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    TeamService.update($scope.team).then(function (data) {
                        $scope.team = data;
                        $scope.team.authorities = team.authorities.split(',');
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);