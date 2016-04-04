var app = {
    templates: {},
    currentTable: null,
    initTemplate: function() {
        $('script[type="text/x-handlebars-template"]').each(function() {
            html = $(this).html();
            name = $(this).attr("id");
            app.templates[name] = Handlebars.compile(html);
        });
    },
    onReady: function() {
        app.initTemplate();
        app.listDB();
        
    },
    listDB: function() {
        sqlite.listDB(function(res) {
            $('.db-list').html(app.templates['template-dbs'](res));
            $('.db').click(function() {
                var name = $(this).data('db-name');
                app.openDB(name);
            });
        });
    },
    openDB: function(name) {
        sqlite.open(name, function(res) {
            if (res.code == 0) {
                app.listTables(name);
            }
        });
    },
    listTables: function(dbName) {
        sqlite.execSQL('select * from sqlite_master', function(res) {
            $('.table-name').remove();
            var html = app.templates['template-tables'](res);
            var button = $('button[data-db-name="' + dbName + '"]');
            $(html).insertAfter(button);
            $('.query-container').show();

            $('.table-name').click(function() {
                var tableName = $(this).data('table-name');
                app.currentTable = tableName;
                $('.sql-input').val('select * from ' + tableName + ';');
                $('.sql-button').click();
            });

            $('.sql-button').click(function() {
                var sql = $('.sql-input').val();
                app.execSQL(sql);
            });

        });
    },
    execSQL: function(sql) {
        var isSelect = sql.split(' ')[0].toLowerCase() == 'select';
        sqlite.execSQL(sql, function(res){
            if (res.code == 0) {
                var content = app.templates['template-query-result'](res);
                $('.query-result').html(content);
                if (!isSelect) {
                    app.execSQL('select * from ' + app.currentTable);
                }
            } else {
                var content = app.templates['template-alert'](res);
                $('.query-result').html(content);
            }
            
        });
    }
}

$(document).ready(function() {
    app.onReady();
});