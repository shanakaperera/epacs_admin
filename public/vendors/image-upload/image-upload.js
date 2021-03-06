(function (e) {
    e.extend({
        uploadPreview: function (t) {
            var n = e.extend({
                input_field: ".image-input",
                preview_box: ".image-preview",
                label_field: ".image-label",
                label_default: "Choose File",
                label_selected: "Change File",
                no_label: false
            }, t);
            if (window.File && window.FileList && window.FileReader) {
                if (typeof e(n.input_field) !== "undefined" && e(n.input_field) !== null) {
                    e(n.input_field).change(function () {
                        var t = this.files;
                        if (t.length > 0) {
                            var r = t[0];
                            var i = new FileReader;
                            i.addEventListener("load", function (t) {
                                var i = t.target;
                                if (r.type.match("image")) {
                                    e(n.preview_box).css("background-image", "url(" + i.result + ")");
                                    e(n.preview_box).css("background-size", "cover");
                                    e(n.preview_box).css("background-position", "center center")
                                } else if (r.type.match("audio")) {
                                    e(n.preview_box).html("<audio controls><source src='" + i.result + "' type='" + r.type + "' />Your browser does not support the audio element.</audio>")
                                } else {
                                    alert("This file type is not supported yet.")
                                }
                            });
                            if (n.no_label == false) {
                                e(n.label_field).html(n.label_selected)
                            }
                            i.readAsDataURL(r)
                        } else {
                            if (n.no_label == false) {
                                e(n.label_field).html(n.label_default)
                            }
                            e(n.preview_box).css("background-image", "none");
                            e(n.preview_box + " audio").remove()
                        }
                    })
                }
            } else {
                alert("You need a browser with file reader support, to use this form properly.");
                return false
            }
        }
    })
})(jQuery)