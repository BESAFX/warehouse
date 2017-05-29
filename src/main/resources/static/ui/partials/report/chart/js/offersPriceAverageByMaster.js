app.controller('offersPriceAverageByMasterCtrl', ['MasterService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.masters = [];
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
            MasterService.fetchTableData().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.mastersList.length; i++) {
                listId[i] = $scope.buffer.mastersList[i].id;
            }
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/ChartOffersPriceAverageByMaster?'
                    + "mastersList=" + listId + "&"
                    + "chartType=" + $scope.buffer.chart.type + "&"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/ChartOffersPriceAverageByMaster?'
                    + "mastersList=" + listId + "&"
                    + "chartType=" + $scope.buffer.chart.type);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);