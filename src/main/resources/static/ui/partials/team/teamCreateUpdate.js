app.controller('teamCreateUpdateCtrl', ['TeamService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'team',
    function (TeamService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, team) {

        $scope.roles = [
            {id: 1, name: 'تعديل بيانات الشركة', value: 'ROLE_COMPANY_UPDATE', selected: false},
            {id: 2, name: 'إنشاء الفروع', value: 'ROLE_BRANCH_CREATE', selected: false},
            {id: 3, name: 'تعديل بيانات الفروع', value: 'ROLE_BRANCH_UPDATE', selected: false},
            {id: 4, name: 'حذف الفروع', value: 'ROLE_BRANCH_DELETE', selected: false},
            {id: 5, name: 'إنشاء التخصصات', value: 'ROLE_MASTER_CREATE', selected: false},
            {id: 6, name: 'تعديل بيانات التخصصات', value: 'ROLE_MASTER_UPDATE', selected: false},
            {id: 7, name: 'حذف التخصصات', value: 'ROLE_MASTER_DELETE', selected: false},
            {id: 8, name: 'إنشاء العروض', value: 'ROLE_OFFER_CREATE', selected: false},
            {id: 9, name: 'تعديل العروض', value: 'ROLE_OFFER_UPDATE', selected: false},
            {id: 10, name: 'حذف العروض', value: 'ROLE_OFFER_DELETE', selected: false},
            {id: 11, name: 'إنشاء الدورات', value: 'ROLE_COURSE_CREATE', selected: false},
            {id: 12, name: 'تعديل الدورات', value: 'ROLE_COURSE_UPDATE', selected: false},
            {id: 13, name: 'حذف الدورات', value: 'ROLE_COURSE_DELETE', selected: false},
            {id: 14, name: 'إنشاء حسابات الطلاب', value: 'ROLE_STUDENT_CREATE', selected: false},
            {id: 15, name: 'تعديل حسابات الطلاب', value: 'ROLE_STUDENT_UPDATE', selected: false},
            {id: 16, name: 'حذف حسابات الطلاب', value: 'ROLE_STUDENT_DELETE', selected: false},
            {id: 17, name: 'إنشاء التسجيلات', value: 'ROLE_ACCOUNT_CREATE', selected: false},
            {id: 18, name: 'تعديل التسجيلات', value: 'ROLE_ACCOUNT_UPDATE', selected: false},
            {id: 19, name: 'حذف التسجيلات', value: 'ROLE_ACCOUNT_DELETE', selected: false},
            {id: 20, name: 'إنشاء سندات القبض', value: 'ROLE_PAYMENT_CREATE', selected: false},
            {id: 21, name: 'تعديل سندات القبض', value: 'ROLE_PAYMENT_UPDATE', selected: false},
            {id: 22, name: 'حذف سندات القبض', value: 'ROLE_PAYMENT_DELETE', selected: false},
            {id: 23, name: 'إنشاء سندات الصرف', value: 'ROLE_PAYMENT_OUT_CREATE', selected: false},
            {id: 24, name: 'تعديل سندات الصرف', value: 'ROLE_PAYMENT_OUT_UPDATE', selected: false},
            {id: 25, name: 'حذف سندات الصرف', value: 'ROLE_PAYMENT_OUT_DELETE', selected: false},
            {id: 26, name: 'إنشاء حسابات البنوك', value: 'ROLE_BANK_CREATE', selected: false},
            {id: 27, name: 'تعديل حسابات البنوك', value: 'ROLE_BANK_UPDATE', selected: false},
            {id: 28, name: 'حذف حسابات البنوك', value: 'ROLE_BANK_DELETE', selected: false},
            {id: 29, name: 'إيداع الي الحساب البنكي', value: 'ROLE_DEPOSIT_CREATE', selected: false},
            {id: 30, name: 'سحب من الحساب البنكي', value: 'ROLE_WITHDRAW_UPDATE', selected: false},
            {id: 31, name: 'إنشاء انواع فواتير الشراء', value: 'ROLE_BILL_BUY_TYPE_CREATE', selected: false},
            {id: 32, name: 'تعديل انواع فواتير الشراء', value: 'ROLE_BILL_BUY_TYPE_UPDATE', selected: false},
            {id: 33, name: 'حذف انواع فواتير الشراء', value: 'ROLE_BILL_BUY_TYPE_DELETE', selected: false},
            {id: 34, name: 'إنشاء فاتورة شراء', value: 'ROLE_BILL_BUY_CREATE', selected: false},
            {id: 35, name: 'تعديل فاتورة شراء', value: 'ROLE_BILL_BUY_UPDATE', selected: false},
            {id: 36, name: 'حذف فاتورة شراء', value: 'ROLE_BILL_BUY_DELETE', selected: false},
            {id: 37, name: 'إنشاء الصلاحيات', value: 'ROLE_TEAM_CREATE', selected: false},
            {id: 38, name: 'تعديل بيانات الصلاحيات', value: 'ROLE_TEAM_UPDATE', selected: false},
            {id: 39, name: 'حذف الصلاحيات', value: 'ROLE_TEAM_DELETE', selected: false},
            {id: 40, name: 'إنشاء المستخدمون', value: 'ROLE_PERSON_CREATE', selected: false},
            {id: 41, name: 'تعديل بيانات المستخدمون', value: 'ROLE_PERSON_UPDATE', selected: false},
            {id: 42, name: 'تفعيل المستخدمون', value: 'ROLE_PERSON_ENABLE', selected: false},
            {id: 43, name: 'تعطيل المستخدمون', value: 'ROLE_PERSON_DISABLE', selected: false},
            {id: 44, name: 'حذف المستخدمون', value: 'ROLE_PERSON_DELETE', selected: false},
            {id: 45, name: 'انشاء حالة للطالب', value: 'ROLE_ACCOUNT_CONDITION_CREATE', selected: false},
            {id: 46, name: 'اضافة مستندات طالب', value: 'ROLE_ACCOUNT_ATTACH_CREATE', selected: false},
            {id: 47, name: 'حذف مستندات طالب', value: 'ROLE_ACCOUNT_ATTACH_DELETE', selected: false},
            {id: 48, name: 'انشاء ملاحظات طالب', value: 'ROLE_ACCOUNT_NOTE_CREATE', selected: false},
            {id: 49, name: 'إنشاء تصنيفات التخصصات', value: 'ROLE_MASTER_CATEGORY_CREATE', selected: false},
            {id: 50, name: 'تعديل بيانات تصنيفات التخصصات', value: 'ROLE_MASTER_CATEGORY_UPDATE', selected: false},
            {id: 51, name: 'حذف تصنيفات التخصصات', value: 'ROLE_MASTER_CATEGORY_DELETE', selected: false},
            {id: 51, name: 'النحكم بكافة الفروع', value: 'ROLE_BRANCH_FULL_CONTROL', selected: false}
        ];


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

        $scope.title = title;

        $scope.action = action;

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

    }]);