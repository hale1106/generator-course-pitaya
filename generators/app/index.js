'use strict';
var yeoman = require('yeoman-generator');
var chalk = require('chalk');
var yosay = require('yosay');
var fs = require("fs");
var mkdirp = require('mkdirp');

module.exports = yeoman.Base.extend({
  initializing: function () {
    this.pkg = require('../../package.json');
  },
  constructor: function () {
    yeoman.Base.apply(this, arguments);
    // This method adds support for a `--coffee` flag
    this.option('sub');
    // And you can then access it later on this way; e.g.
    this.pageSuffix = !!this.options.sub;
  },

  prompting: function () {
    var done = this.async();

    // Have Yeoman greet the user.
    this.log(yosay(
      'Welcome to the peachy ' + chalk.red('Pitaya') + ' generator!'
    ));
    //this.log(this);

    var prompts = [{
      type: 'list',
      name: 'type',
      message: '你的项目类型?',
      choices:['h5','java'],
      default: 0
    },{
      type: 'input',
      name: 'name',
      message: '你的主项目名称?',
      default: this.appname
    },{
      type: 'input',
      name: 'subName',
      message: '你的子项目名称?',
      default: this.appname
    }];

    this.prompt(prompts, function (props) {
      this.props = props;
      // To access props later use this.props.someOption;
      done();
    }.bind(this));
  },

  writing: {
    app: function () {
      var props = this.props;
      if(props.type==='h5'){
        //生成主项目
        if(!this.pageSuffix){
          mkdirp(this.destinationPath(props.name));
          this.directory(
            this.templatePath('h5/sub'),
            this.destinationPath(props.name+'/'+props.subName)
          );
          this.template('h5/sub/fis-conf.js',props.name+'/'+props.subName+'/fis-conf.js');
          this.template('h5/sub/package.json',props.name+'/'+props.subName+'/package.json');
          this.template('h5/sub/deploy.bat',props.name+'/'+props.subName+'/deploy.bat');
          this.template('h5/sub/src/index.html',props.name+'/'+props.subName+'/src/index.html');
          this.template('h5/sub/gitignore',props.name+'/'+props.subName+'/.gitignore');
        }else{
          this.directory(
            this.templatePath('h5/sub'),
            this.destinationPath(props.subName)
          );
          this.template('h5/sub/fis-conf.js',props.subName+'/fis-conf.js');
          this.template('h5/sub/package.json',props.subName+'/package.json');
          this.template('h5/sub/deploy.bat',props.subName+'/deploy.bat');
          this.template('h5/sub/src/index.html',props.subName+'/src/index.html');
        }
      }else if(props.type === 'java'){
        //生成主项目
        if(!this.pageSuffix) {
          mkdirp(this.destinationPath(props.name));
          this.directory(
            this.templatePath('java/.settings'),
            this.destinationPath(props.name+'/.settings')
          );
          this.directory(
            this.templatePath('java/src'),
            this.destinationPath(props.name+'/src')
          );
          this.directory(
            this.templatePath('java/target'),
            this.destinationPath(props.name+'/target')
          );
          this.directory(
            this.templatePath('java/webapp'),
            this.destinationPath(props.name+'/webapp')
          );
          this.directory(
            this.templatePath('java/configs'),
            this.destinationPath(props.name)
          );
          mkdirp(props.name+'/fe-source');
          mkdirp(props.name+'/fe-source/page');
          mkdirp(props.name+'/fe-source/static');
          mkdirp(props.name+'/fe-source/static/css');
          mkdirp(props.name+'/fe-source/static/js');
          mkdirp(props.name+'/fe-source/static/images');
          mkdirp(props.name+'/fe-source/test');
          mkdirp(props.name+'/fe-source/test/page');
          this.directory(
            this.templatePath('java/fe-source/page/sub'),
            this.destinationPath(props.name+'/fe-source/page/'+props.subName)
          );
          this.directory(
            this.templatePath('java/fe-source/static/css/sub'),
            this.destinationPath(props.name+'/fe-source/static/css/'+props.subName)
          );
          this.directory(
            this.templatePath('java/fe-source/static/js/sub'),
            this.destinationPath(props.name+'/fe-source/static/js/'+props.subName)
          );
          this.directory(
            this.templatePath('java/fe-source/static/images/sub'),
            this.destinationPath(props.name+'/fe-source/static/images/'+props.subName)
          );
          this.directory(
            this.templatePath('java/fe-source/test/page/sub'),
            this.destinationPath(props.name+'/fe-source/test/page/'+props.subName)
          );
          this.directory(
            this.templatePath('java/fe-source/static/js/common'),
            this.destinationPath(props.name+'/fe-source/static/js/common')
          );
          this.fs.copy(
            this.templatePath('java/fe-source/static/css/global.less'),
            this.destinationPath(props.name+'/fe-source/static/css/global.less')
          );
          this.template('java/fe-source/fis-conf.js', props.name + '/fe-source/fis-conf.js');
          this.template('java/fe-source/server.conf', props.name + '/fe-source/server.conf');
          this.template('java/fe-source/page/contents.vm', props.name + '/fe-source/page/contents.vm');
          this.template('java/fe-source/page/sub/layout.vm', props.name + '/fe-source/page/'+props.subName+'/layout.vm');
          this.template('java/fe-source/page/sub/index.vm', props.name + '/fe-source/page/'+props.subName+'/index.vm');
          this.template('java/configs/package.json', props.name + '/package.json');
          this.template('java/configs/.project', props.name + '/.project');
          this.template('java/configs/deploy.bat', props.name + '/deploy.bat');
          this.template('java/configs/pom.xml', props.name + '/pom.xml');
          this.template('java/src/main/resources/config.properties', props.name + '/src/main/resources/config.properties');
          this.template('java/src/main/resources/config_online.properties', props.name + '/src/main/resources/config_online.properties');
          this.template('java/src/main/resources/log4j.properties', props.name + '/src/main/resources/log4j.properties');
          this.template('java/src/main/resources/log4j_online.properties', props.name + '/src/main/resources/log4j_online.properties');
          this.template('java/.settings/org.eclipse.wst.common.component', props.name + '/.settings/org.eclipse.wst.common.component');
          this.template('java/gitignore', props.name + '/.gitignore');
          this.template('java/fe-source/static/css/sub/main.less', props.name + '/fe-source/static/css/'+props.subName+'/main.less');
        }else{ //生成子项目
          this.fs.copy(
            this.templatePath('java/fe-source/page/sub'),
            this.destinationPath('page/'+props.subName)
          );
          this.fs.copy(
            this.templatePath('java/fe-source/static/css/sub'),
            this.destinationPath('static/css/'+props.subName)
          );
          this.fs.copy(
            this.templatePath('java/fe-source/static/images/sub'),
            this.destinationPath('static/images/'+props.subName)
          );
          this.fs.copy(
            this.templatePath('java/fe-source/static/js/sub'),
            this.destinationPath('static/js/'+props.subName)
          );
          this.fs.copy(
            this.templatePath('java/fe-source/test/page/sub'),
            this.destinationPath('test/page/'+props.subName)
          );
          this.template('java/fe-source/page/sub/layout.vm', 'page/'+props.subName+'/layout.vm');
          this.template('java/fe-source/page/sub/index.vm', 'page/'+props.subName+'/index.vm');
        }
      }
    }
    /*projectfiles: function () {
      this.fs.copy(
        this.templatePath('gitignore'),
        this.destinationPath('.gitignore')
      );
      this.fs.copy(
        this.templatePath('jshintrc'),
        this.destinationPath('.jshintrc')
      );
    }*/
  },

  install: function () {
    //this.installDependencies();
  },
  end:function(){
    this.log('Complete! Good Luck!');
  }
});
