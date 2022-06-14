$(function () {
    if (jQuery.browser.msie) {
        if (jQuery.browser.version < 9) {
            //alert('抱歉,本系统的最佳浏览效果不支持IE8之前的浏览器版本');
            alert("本系统不支持IE9之前的浏览器版本,请升级您的浏览器或使用360浏览器极速模式.");
            //return false;
        }
    }
//support placeholder
    if (jQuery.browser.msie) {
        if (jQuery.browser.version < 10) {
            if (!('placeholder' in document.createElement('input'))) {
                $('input[placeholder],textarea[placeholder]').each(function () {
                    var that = $(this),
                        text = that.attr('placeholder');
                    if (that.val() === "") {
                        that.val(text).addClass('placeholder');
                    }
                    that.focus(function () {
                        if (that.val() === text) {
                            that.val("").removeClass('placeholder');
                        }
                    })
                        .blur(function () {
                            if (that.val() === "") {
                                that.val(text).addClass('placeholder');
                            }
                        })
                        .closest('form').submit(function () {
                        if (that.val() === text) {
                            that.val('');
                        }
                    });
                });
            }
        }
    }
});

JSON.unflatten = function (data) {
    "use strict";
    if (Object(data) !== data || Array.isArray(data))
        return data;
    var regex = /\.?([^.\[\]]+)|\[(\d+)\]/g,
        resultholder = {};
    for (var p in data) {
        var cur = resultholder,
            prop = "",
            m;
        while (m = regex.exec(p)) {
            cur = cur[prop] || (cur[prop] = (m[2] ? [] : {}));
            prop = m[2] || m[1];
        }
        cur[prop] = data[p];
    }
    return resultholder[""] || resultholder;
};
JSON.flatten = function (data) {
    var result = {};

    function recurse(cur, prop) {
        if (Object(cur) !== cur) {
            result[prop] = cur;
        } else if (Array.isArray(cur)) {
            for (var i = 0, l = cur.length; i < l; i++)
                recurse(cur[i], prop + "[" + i + "]");
            if (l == 0)
                result[prop] = [];
        } else {
            var isEmpty = true;
            for (var p in cur) {
                isEmpty = false;
                recurse(cur[p], prop ? prop + "." + p : p);
            }
            if (isEmpty && prop)
                result[prop] = {};
        }
    }

    recurse(data, "");
    return result;
}
