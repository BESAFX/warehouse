app.directive('loading', ['$http', function ($http) {
    return {
        restrict: 'A',
        link: function (scope, elm, attrs) {
            scope.isLoading = function () {
                return $http.pendingRequests.length > 0;
            };
            scope.$watch(scope.isLoading, function (v) {
                if (v) {
                    if ($.mpb) {
                        $.mpb("show", {value: [0, 50], speed: 5});
                    }
                } else {
                    if ($.mpb) {
                        $.mpb("update", {
                            value: 100, speed: 5, complete: function () {
                                $(".mpb").fadeOut(200, function () {
                                    $(this).remove();
                                });
                            }
                        });
                    }
                }
            });
        }
    };

}]);

app.directive('besaDate', function () {
    return {
        restrict: 'AE',
        replace: true,
        require: 'ngModel',
        scope: {
            date: '=ngModel'
        },
        template: '<div></div>',
        link: function (scope, iEmlement, iAttrs) {
            scope.scope = scope;
            var calender = new Calendar(true, 0, false, true);
            var calenderMode = calender.isHijriMode();
            iEmlement[0].appendChild(calender.getElement());
            calender.show();
            calender.callback = function () {
                if (calenderMode !== calender.isHijriMode()) {
                    calenderMode = calender.isHijriMode();
                }
                scope.date = calender.getDate().getGregorianDate();
            };
            scope.$watch('date', function (newValue, oldValue) {
                calender.setTime(new Date(scope.date).getTime());
            }, true);

        }
    };
});

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });
                event.preventDefault();
            }
        });
    };
});

app.directive('removeIf', ['$parse',
    function ($parse) {
        return function (scope, element, attr) {
            if ($parse(attr.removeIf)(scope)) {
                element.remove();
            }
        }
    }
]);

app.directive('resize', ['$window',
    function ($window) {
        return function (scope, element, attr) {

            var w = angular.element($window);
            scope.$watch(function () {
                return {
                    'h': window.innerHeight,
                    'w': window.innerWidth
                };
            }, function (newValue, oldValue) {
                scope.windowHeight = newValue.h;
                scope.windowWidth = newValue.w;

                scope.resizeWithOffset = function (offsetH) {
                    scope.$eval(attr.notifier);
                    return {
                        'height': (newValue.h - offsetH) + 'px'
                    };
                };

            }, true);

            w.bind('resize', function () {
                if (!scope.$$phase) {
                    scope.$apply();
                }
            });
        }
    }
]);

app.directive("formatDate", function () {
    return {
        require: 'ngModel',
        link: function (scope, elem, attr, modelCtrl) {
            modelCtrl.$formatters.push(function (modelValue) {
                if (modelValue) {
                    return new Date(modelValue);
                }
            })
        }
    }
});

app.directive('ngThumb', ['$window', function ($window) {
    var helper = {
        support: !!($window.FileReader && $window.CanvasRenderingContext2D),
        isFile: function (item) {
            return angular.isObject(item) && item instanceof $window.File;
        },
        isImage: function (file) {
            var type = '|' + file.type.slice(file.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    };

    return {
        restrict: 'A',
        template: '<canvas/>',
        link: function (scope, element, attributes) {
            if (!helper.support) return;

            var params = scope.$eval(attributes.ngThumb);

            if (!helper.isFile(params.file)) return;
            if (!helper.isImage(params.file)) return;

            var canvas = element.find('canvas');
            var reader = new FileReader();

            reader.onload = onLoadFile;
            reader.readAsDataURL(params.file);

            function onLoadFile(event) {
                var img = new Image();
                img.onload = onLoadImage;
                img.src = event.target.result;
            }

            function onLoadImage() {
                var width = params.width || this.width / this.height * params.height;
                var height = params.height || this.height / this.width * params.width;
                canvas.attr({width: width, height: height});
                canvas[0].getContext('2d').drawImage(this, 0, 0, width, height);
            }
        }
    };
}]);

app.directive('stSelectRowCustom',['$timeout',
    function ($timeout) {
    return {
        restrict: 'A',
        require: '^stTable',
        scope: {
            row: '=stSelectRowCustom',
            callback: '&stSelected' // ADDED THIS
        },
        link: function (scope, element, attr, ctrl) {
            var mode = attr.stSelectMode || 'single';
            element.bind('click', function ($event) {
                scope.$apply(function () {
                    ctrl.select(scope.row, mode, $event.shiftKey);
                    scope.callback(); // AND THIS
                    $timeout(function () {
                        window.componentHandler.upgradeAllRegistered();
                    }, 500);
                });
            });

            element.bind('contextmenu', function ($event) {
                scope.$apply(function () {
                    ctrl.select(scope.row, mode, $event.shiftKey);
                    scope.callback(); // AND THIS
                    $timeout(function () {
                        window.componentHandler.upgradeAllRegistered();
                    }, 500);
                });
            });

            scope.$watch('row.isSelected', function (newValue) {
                if (newValue === true) {
                    element.parent().addClass('success');
                } else {
                    element.parent().removeClass('success');
                }
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            });
        }
    };
}
]);

app.directive('stSelectRowMulti', ['$timeout',
    function ($timeout) {
        return {
            restrict: 'A',
            require: '^stTable',
            scope: {
                row: '=stSelectRowMulti',
                callback: '&stSelected' // ADDED THIS
            },
            link: function (scope, element, attr, ctrl) {
                element.bind('click', function (eventType) {
                    scope.$apply(function () {
                        // ctrl.select(scope.row, 'multiple');
                        ctrl.select(scope.row, 'multiKey', eventType.shiftKey, eventType.ctrlKey);
                        scope.callback(); // AND THIS
                        $timeout(function () {
                            window.componentHandler.upgradeAllRegistered();
                        }, 500);
                    });
                });

                element.bind('contextmenu', function (eventType) {
                    scope.$apply(function () {
                        // ctrl.select(scope.row, 'multiple');
                        ctrl.select(scope.row, 'multiKey', eventType.shiftKey, eventType.ctrlKey);
                        scope.callback(); // AND THIS
                        $timeout(function () {
                            window.componentHandler.upgradeAllRegistered();
                        }, 500);
                    });
                });

                scope.$watch('row.isSelected', function (newValue) {
                    if (newValue === true) {
                        element.parent().addClass('success');
                    } else {
                        element.parent().removeClass('success');
                    }
                    $timeout(function () {
                        window.componentHandler.upgradeAllRegistered();
                    }, 500);
                });
            }
        };
    }
]);

app.directive('stDefaultValue', function () {
    return {
        restrict: 'A',
        require: '^stTable',
        scope: {
            selectedRow: '=stDefaultValue',
            stSafeSrc: '='
        },
        link: function (scope, element, attr, ctrl) {
            scope.$watch('stSafeSrc', function (newValue, oldValue) {
                if (typeof newValue !== 'undefined') {
                    ctrl.select(newValue[scope.selectedRow], 'single', false);
                }
            });
        }
    };
});

app.directive('ngRightClick', ['$parse',
    function ($parse) {
        return function (scope, element, attrs) {
            var fn = $parse(attrs.ngRightClick);
            element.bind('contextmenu', function (event) {
                scope.$apply(function () {
                    event.preventDefault();
                    fn(scope, {$event: event});
                });
            });
        };
    }
]);

app.directive('arrowSelector',['$document',function($document){
    return{
        restrict:'A',
        link:function(scope,elem,attrs,ctrl){
            var elemFocus = false;
            elem.on('mouseenter',function(){
                elemFocus = true;
                console.log(elemFocus);
            });
            elem.on('mouseleave',function(){
                elemFocus = false;
                console.log(elemFocus);
            });
            $document.bind('keydown',function(e){
                if(elemFocus){
                    if(e.keyCode == 38){
                        console.log(scope.selectedRow);
                        if(scope.selectedRow == 0){
                            return;
                        }
                        scope.selectedRow--;
                        scope.$apply();
                        e.preventDefault();
                    }
                    if(e.keyCode == 40){
                        if(scope.selectedRow == scope.foodItems.length - 1){
                            return;
                        }
                        scope.selectedRow++;
                        scope.$apply();
                        e.preventDefault();
                    }
                }
            });
        }
    };
}]);

app.directive("formatDate", function(){
    return {
        require: 'ngModel',
        link: function(scope, elem, attr, modelCtrl) {
            modelCtrl.$formatters.push(function(modelValue){
                return new Date(modelValue);
            })
        }
    }
});

app.directive('csSelect', function () {
    return {
        require: '^stTable',
        template: '<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" ng-for="checkbox{{$index+1}}"><input type="checkbox" ng-id="checkbox{{$index+1}}" class="mdl-checkbox__input"></label>',
        scope: {
            row: '=csSelect'
        },
        link: function (scope, element, attr, ctrl) {

            element.bind('change', function (evt) {
                scope.$apply(function () {
                    ctrl.select(scope.row, 'multiple');
                });
            });

            scope.$watch('row.isSelected', function (newValue, oldValue) {
                if (newValue === true) {
                    element.parent().addClass('success');
                } else {
                    element.parent().removeClass('success');
                }
            });
        }
    };
});


