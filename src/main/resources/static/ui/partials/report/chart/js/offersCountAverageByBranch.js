app.controller('offersCountAverageByBranchCtrl', ['BranchService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.branches = [];
            $scope.buffer.chart = {};
            $scope.charts =
                [
                    {"id": 1, "name": 'مخطط دائري', "class": 'fa fa-pie-chart fa-lg', "type": 'PIE_CHART'},
                    {
                        "id": 2,
                        "name": 'مخطط دائري ثلاثي الأبعاد',
                        "class": 'fa fa-pie-chart fa-lg',
                        "type": 'PIE_CHART_3D'
                    },
                    {"id": 3, "name": 'مخطط شريطي', "class": 'fa fa-bar-chart fa-lg', "type": 'BAR_CHART'},
                    {
                        "id": 4,
                        "name": 'مخطط شريطي ثلاثي الأبعاد',
                        "class": 'fa fa-bar-chart fa-lg',
                        "type": 'BAR_CHART_3D'
                    }
                ];
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.branchesList.length; i++) {
                listId[i] = $scope.buffer.branchesList[i].id;
            }
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/ChartOffersCountAverageByBranch?'
                    + "branchesList=" + listId + "&"
                    + "chartType=" + $scope.buffer.chart.type + "&"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/ChartOffersCountAverageByBranch?'
                    + "branchesList=" + listId + "&"
                    + "chartType=" + $scope.buffer.chart.type);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);