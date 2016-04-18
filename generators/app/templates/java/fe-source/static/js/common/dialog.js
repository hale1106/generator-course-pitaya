var Dialog =function(){
  this.win = $(window);
  this.doc = $(document.body);
  this.winH = this.win.height();
  this.docH = this.doc.height();
  this.overlay = $('<div style="width:100%;height:'+this.docH+'px;background:#000;opacity:0.75;position:absolute;top:0;left:0;z-index:999"></div>');
  this.content = $('<div style="position:absolute;z-index:1000;"></div>');
  this.showflg = false;
  var _this =this;
  this.win.resize(function(){
    _this.overlay.css({'height':_this.doc.height()});
  });
};
Dialog.prototype={
  addOverlay:function(){
    var _this = this;
    this.showFlg ? this.overlay.show() : _this.overlay.appendTo(_this.doc);
    return _this.overlay;
  },
  removeOverlay:function(){
    var _this = this;
    _this.overlay.remove();
    return _this;
  },
  setBody:function(html){
    var _this = this;
    _this.content.append(html);
    return _this;
  },
  show:function(){
    var _this = this;
    this.showFlg ? this.content.show() : this.content.appendTo(_this.doc);
    var _h = _this.content.children().height();
    var _w = _this.content.children().width();
    var _t = _this.win.scrollTop();
    this.content.css({'width':'100%','height':'100%','display':'box','display':'-ms-box','display':'-webkit-box','box-orient':'vertical','-webkit-box-orient':'vertical','-ms-box-orient':'vertical','box-pack':'center','-webkit-box-pack':'center','-ms-box-pack':'center','box-align':'center','-webkit-box-align':'center','-ms-box-align':'center','top':0,'left':0});
    this.addOverlay();
    this.showFlg = false;
    return this;
  },
  remove:function(){
    this.content.remove();
    this.removeOverlay();
    return this;
  },
  hide:function(){
    this.content.hide();
    this.overlay.hide();
    this.showFlg = true;
    return this;
  }
};

var alert_v = function(str,btn,callback){
  var _btn = btn || '确定';
  if(typeof btn === 'function'){
    _btn = '确定';
    callback = btn;
  }
  var html = $('<div style="width:200px;background:#701324;border:1px solid #e20731;border-radius:5px;padding:20px;"><div style="text-align:center;color:#fff">'+str+'</div><div style="text-align:center;margin-top:15px;"><a href="javascript:;" style="background:#e20731;width:100px;height:25px;display:inline-block;line-height:25px;color:#fff;border:1px solid #b60325;border-radius:5px" id="closeAlert">'+_btn+'</a></div></div>');
  var dig = new Dialog().setBody(html).show();
  $('#closeAlert').on('tap',function(){
    dig.remove();
    dig = null;
    callback && callback();
  });
};

var Loading = function(str){
  this.html = $('<div style="width:200px;background:#701324;border:1px solid #e20731;border-radius:5px;padding:20px;"><div style="text-align:center;color:#fff">'+str+'</div></div>');
  this.dig = null;
};
Loading.prototype.show =function(){
  if(!this.dig){
    this.dig = new Dialog().setBody(this.html).show();
  }
  return this;
};
Loading.prototype.remove =function(){
  this.dig.remove();
  this.dig = null;
};
Loading.prototype.dailyRemove =function(time){
  var _this =this;
  setTimeout(function(){
    _this.remove();
  },1000);
};