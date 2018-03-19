app.controller('offerByIdCtrl', ['OfferService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (OfferService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.offers = [];

        $scope.searchOffer = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.page = 0;
                $scope.offers = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.page++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.page);
            search.push('&');

            switch ($scope.buffer.searchBy){
                case "code":{
                    search.push('codeFrom=');
                    search.push($select.search);
                    search.push('&');
                    search.push('codeTo=');
                    search.push($select.search);
                    search.push('&');
                    break;
                }
                case "customerName":{
                    search.push('customerName=');
                    search.push($select.search);
                    search.push('&');
                    break;
                }
                case "customerIdentityNumber":
                    search.push('customerIdentityNumber=');
                    search.push($select.search);
                    search.push('&');
                    break;
                case "customerMobile":
                    search.push('customerMobile=');
                    search.push($select.search);
                    search.push('&');
                    break;

            }

            return OfferService.filter(search.join("")).then(function (data) {
                $scope.buffer.last = data.last;
                return $scope.offers = $scope.offers.concat(data.content);
            });

        };

        $scope.submit = function () {
            window.open('/report/OfferById/' + $scope.buffer.offer.id + '?exportType=' + $scope.buffer.exportType);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);