var sqlite = {
    listDB: function(callback) {
        $.getJSON('/listDB', function(res) {
            if (callback) {
                callback(res);
            }
        });
    },

    execSQL: function(sql, callback) {
        $.getJSON('/execSQL', {sql: sql}, function(res) {
            if (callback) {
                callback(res);
            }
        });
    },

    open: function(name, callback) {
        $.getJSON('/open', {name: name}, function(res) {
            if (callback) {
                callback(res);
            }
        });
    }
}