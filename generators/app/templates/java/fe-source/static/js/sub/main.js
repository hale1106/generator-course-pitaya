/*
* init：对象初始化（已在loading.js加载图片后调用）
*
* */
var Main = function(){

};

Main.prototype = {
  constructor: "Main",
  init: function(){
    this.handle();
  },
  handle:function(){
    this.getName();
    //this.getAge();
  },
  getName:function(){
    $.ajax({
      url:'/getName',
      type:'get',
      dataType:'json',
      cache:false,
      success:function(res){
        if(res.status === 0){
          alert('排名显示完成');
        }else{
          alert(res.message || 'error');
        }
      }
    })
  }
};


var main = new Main().init();