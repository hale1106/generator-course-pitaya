1.	相关API
	*	grunt demo
		*	启动调试服务，不做压缩，去缓存处理，默认启动9000端口，如需改端口，可在abc.json里修改port
		*	自动监听文件变动，调试时修改文件直接刷新浏览
	*	grunt debug
		*	启动调试服务，会做压缩，去缓存处理，完全模拟线上环境，默认启动9000端口，如需改端口，可在abc.json里修改port
		*	自动监听文件变动，调试时修改文件直接刷新浏览
	*	grunt build
		*	构建并在项目根目录下生成压缩、加md5去缓存后完整项目
		*	然后就可以用该目录发布上线啦
	*	grunt newbranch
		*	用于新建并切换git分支
		*	功能相当于git checkout -b branch_name，但自动会规则分支名形如daily/0.0.1自加
	*	grunt prepub
		*	用于提交该分支到git仓库
		*	可在命令后加参数做成提交的日志，如grunt prepub:'up date'
		*	功能相当于git add . ; git commit -m 'up date' ; git push origin branch_name;
	*	grunt publish
		*	用于提交分支、加tag、切换到master、更新并提交master
		*	可在命令后加参数做成提交的日志，如grunt publish:'up date'
		*	功能相当于git tag tag_name;git push origin tag_name:tag_name; git checkout master; git pull orign branch_name; git push origin master
		*	注意：在发布到master后，开发前务必先用grunt newbranch新增分支，在分支上开发后再提交
    *   grunt minimage
        *   用于图片压缩
        *   请在grunt build后执行，默认会压缩build后的目录中的图片
