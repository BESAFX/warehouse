app.filter('hijri', function () {
    return function (item) {
        if (item) {
            return new HijriDate(new Date(item).getTime()).getDateString();
        } else {
            return "---";
        }
    };
});

app.filter('hijriYearShortcut', function ($log) {
    return function (item) {
        if (item) {
            var value = new HijriDate(new Date(item).getTime()).getFullYearString().slice(-2);
            return value;
        } else {
            return "---";
        }
    };
});

app.filter('hijriWithTime', function ($log) {
    return function (item) {
        if (item) {
            var date = new Date(item);
            var hours = date.getHours();
            var minutes = date.getMinutes();
            var ampm = hours >= 12 ? 'مساءاً' : 'صباحاَ';
            hours = hours % 12;
            hours = hours ? hours : 12; // the hour '0' should be '12'
            minutes = minutes < 10 ? '0' + minutes : minutes;
            var strTime = hours + ':' + minutes + ' ' + ampm;
            var dateString = new HijriDate(new Date(item).getTime()).getDateString();
            return dateString + " - " + strTime;
        } else {
            return "---";
        }
    };
});

/**
 * AngularJS default filter with the following expression:
 * "person in people | filter: {name: $select.search, age: $select.search}"
 * performs an AND between 'name: $select.search' and 'age: $select.search'.
 * We want to perform an OR.
 */
app.filter('propsFilter', function () {
    return function (items, props) {
        var out = [];

        if (angular.isArray(items)) {
            var keys = Object.keys(props);

            items.forEach(function (item) {
                var itemMatches = false;

                for (var i = 0; i < keys.length; i++) {
                    var prop = keys[i];
                    var text = props[prop].toLowerCase();
                    if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                        itemMatches = true;
                        break;
                    }
                }

                if (itemMatches) {
                    out.push(item);
                }
            });
        } else {
            // Let the output be the input untouched
            out = items;
        }

        return out;
    };
});