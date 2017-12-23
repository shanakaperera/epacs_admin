
(function($) {

    $.fn.iconPicker = function( options ) {

        var mouseOver=false;
        var $popup=null;
        var icons=new Array("adjust","align-center","align-justify","align-left","align-right","arrow-down","arrow-left","arrow-right","arrow-up","asterisk","backward","ban","barcode","bell","bold","book","bookmark","briefcase","bullhorn","calendar","camera","certificate","check","chevron-down","chevron-left","chevron-right","chevron-up","arrow-circle-down","arrow-circle-left","arrow-circle-right","arrow-circle-up","cloud","cloud-download","cloud-upload","cog","caret-square-o-down","caret-square-o-left","caret-square-o-right","caret-square-o-up","comment","compress","copyright","credit-card","cutlery","dashboard","download","cloud-download","phone","headphones","edit","eject","envelope","eur","exclamation","exclamation-triangle","exclamation-circle","expand","eye","eyedropper","eye-slash","video-camera","fast-backward","forward","step-forward","stop","pause","play","backward","fast-backward","step-backward","file","film","filter","fire","flag","flash","floppy-o","folder","folder-open","folder-o","folder-open-o","font","arrows-alt","gbp","gift","glass","globe","hand-o-down","hand-o-up","hand-o-left","hand-o-right","thumbs-down","thumbs-up","thumbs-o-down","thumbs-o-down","hourglass","hourglass-end","hourglass-half","hourglass-start","hourglass-o","hdd-o","header","heart","heartbeat","heart-o","medkit","home","inbox","indent","align-right","align-center","align-left","align-justify","info","info-circle","question","question-circle","italic","leaf","link","list","list-ol","list-ul","list-alt","th-list","bars","lock","unlock-alt","unlock","sign-in","sign-out","magnet","map","map-o","map-marker","map-signs","map-pin","street-view","location-arrow","minus","minus-circle","minus-square","minus-square-o","arrows","arrows-alt","music","external-link","external-link-square","power-off","check","check-circle","check-circle-o","check-square","check-square-o","paperclip","pause","pause-circle","pause-circle-o","pencil","pencil-square-o","pencil-square","phone","phone-square","picture-o","file-image-o","plane","paper-plane-o","paper-plane","play","play-circle","plus","plus-circle","plus-square-o","plus-square","print","qrcode","question","question-circle","question-circle-o","random","refresh","registered","times","repeat","expand","arrows-h","arrows-v","retweet","road","desktop","search","search-minus","search-plus","send","share","share-alt","shopping-cart","signal","sort","sort-numeric-desc","sort-numeric-asc","sort-amount-desc","sort-amount-asc","sort-alpha-desc","sort-alpha-asc","sort-asc","sort-desc","soundcloud","star","star-o","star-half","star-half-o","tag","tags","text-height","text-width","th","tint","exchange","trash","trash-o","tree","upload","usd","user","volume-down","volume-off","volume-up","exclamation","exclamation-triangle","exclamation-circle","wrench","search-plus","search-minus");
        var settings = $.extend({

        }, options);
        return this.each( function() {
            element=this;
            if(!settings.buttonOnly && $(this).data("iconPicker")==undefined ){
                $this=$(this).addClass("form-control");
                $wraper=$("<div/>",{class:"input-group"});
                $this.wrap($wraper);

                $button=$("<span class=\"input-group-addon pointer\"><i class=\"fa fa-picture-o blue\"></i></span>");
                $this.after($button);
                (function(ele){
                    $button.click(function(){
                        createUI(ele);
                        showList(ele,icons);
                    });
                })($this);

                $(this).data("iconPicker",{attached:true});
            }

            function createUI($element){
                $popup=$('<div/>',{
                    css: {
                        'top':$element.offset().top+$element.outerHeight()+6,
                        'left':$element.offset().left
                    },
                    class:'icon-popup'
                })

                $popup.html('<div class="ip-control"> \
						          <ul> \
						            <li><a href="javascript:;" class="btn" data-dir="-1"><span class="fa fa-fast-backward"></span></a></li> \
						            <li><input type="text" class="ip-search fa fa-search" placeholder="Search" /></li> \
						            <li><a href="javascript:;"  class="btn" data-dir="1"><span class="fa fa-fast-forward"></span></a></li> \
						          </ul> \
						      </div> \
						     <div class="icon-list"> </div> \
					         ').appendTo("body");


                $popup.addClass('dropdown-menu').show();
                $popup.mouseenter(function() {  mouseOver=true;  }).mouseleave(function() { mouseOver=false;  });

                var lastVal="", start_index=0,per_page=40,end_index=start_index+per_page;
                $(".ip-control .btn",$popup).click(function(e){
                    e.stopPropagation();
                    var dir=$(this).attr("data-dir");
                    start_index=start_index+per_page*dir;
                    start_index=start_index<0?0:start_index;
                    if(start_index+per_page<=270){
                        $.each($(".icon-list>ul li"),function(i){
                            if(i>=start_index && i<start_index+per_page){
                                $(this).show();
                            }else{
                                $(this).hide();
                            }
                        });
                    }else{
                        start_index=180;
                    }
                });

                $('.ip-control .ip-search',$popup).on("keyup",function(e){
                    if(lastVal!=$(this).val()){
                        lastVal=$(this).val();
                        if(lastVal==""){
                            showList(icons);
                        }else{
                            showList($element, $(icons)
                                .map(function(i,v){
                                    if(v.toLowerCase().indexOf(lastVal.toLowerCase())!=-1){return v}
                                }).get());
                        }

                    }
                });
                $(document).mouseup(function (e){
                    if (!$popup.is(e.target) && $popup.has(e.target).length === 0) {
                        removeInstance();
                    }
                });

            }
            function removeInstance(){
                $(".icon-popup").remove();
            }
            function showList($element,arrLis){
                $ul=$("<ul>");

                for (var i in arrLis) {
                    $ul.append("<li><a href=\"#\" title="+arrLis[i]+"><span class=\"fa fa-"+arrLis[i]+"\"></span></a></li>");
                };

                $(".icon-list",$popup).html($ul);
                $(".icon-list li a",$popup).click(function(e){
                    e.preventDefault();
                    var title=$(this).attr("title");
                    $element.val("fa fa-"+title);
                    removeInstance();
                });
            }

        });
    }

}(jQuery));