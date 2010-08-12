Ext.ns('Docs');

Docs.ApiTreePanel = Ext.extend(Ext.tree.TreePanel, {
    rootVisible:false,
    lines:false,
    autoScroll:true,
    animCollapse:false,
    animate: false,
    constructor : function(cfg) {

        cfg = Ext.applyIf(cfg || {},{
            hiddenPkgs : [], // custom item
            collapseFirst:false,
            tbar : this.buildTbar(),
            loader: new Ext.tree.TreeLoader({
                preloadChildren: true,
                clearOnLoad: false
            }),
            root: new Ext.tree.AsyncTreeNode({
                text:'Ext JS',
                id:'root',
                expanded:true,
                children:[Docs.classData]
             })
        });

        this.constructor.superclass.constructor.call(this,cfg);

        this.getSelectionModel().on('beforeselect', function(sm, node){
            return node.isLeaf();
        });
    },
    buildTbar : function() {
        return [
			{
				xtype: 'textfield',
                width: 200,
				emptyText:'Find a Class',
                enableKeyEvents: true,
				listeners:{
					scope: this,
                    render: function(f){
                    	this.filter = new Ext.tree.TreeFilter(this, {
                    		clearBlank: true,
                    		autoClear: true
                    	});
					},
                    keydown: {
                        fn: this.onTextFieldKeyDown,
                        buffer: 350
                    }
				}
			},
            '->',
			{
                iconCls: 'icon-expand-all',
				tooltip: 'Expand All',
                handler: function(){ this.root.expand(true); },
                scope: this
            },
            '-', {
                iconCls: 'icon-collapse-all',
                tooltip: 'Collapse All',
                handler: function(){ this.root.collapse(true); },
                scope: this
            }
        ];
    },
	onTextFieldKeyDown: function(t, e){
		var text = t.getValue();
		Ext.each(this.hiddenPkgs, function(n){
			n.ui.show();
		});
		if(!text){
			this.filter.clear();
            this.collapseAll();
			return;
		}
        else {
            this.expandAll();
        }
		
		var re = new RegExp('^' + Ext.escapeRe(text), 'i');
        
		this.filter.filterBy(function(n){
			return !n.attributes.isClass || re.test(n.text);
		});
		
		// hide empty packages that weren't filtered
		this.hiddenPkgs = [];
		this.root.cascade(function(n){
			if(!n.attributes.isClass && n.ui.ctNode.offsetHeight < 3){
				n.ui.hide();
				this.hiddenPkgs.push(n);
			}
		});
	},
    
    selectClass : function(cls){
        if(cls){
            var parts = cls.split('.'),
                last = parts.length-1,
                res = [],
                pkg = [],
                i   = 0;

            for(; i < last; i++){ // things get nasty - static classes can have .
                var p = parts[i];
                var fc = p.charAt(0);
                var staticCls = fc.toUpperCase() == fc;
                if(p == 'Ext' || !staticCls){
                    pkg.push(p);
                    res[i] = 'pkg-'+pkg.join('.');
                }else if(staticCls){
                    --last;
                    res.splice(i, 1);
                }
            }
            res[last] = cls;

            this.selectPath('/root/apidocs/'+res.join('/'));
        }
    }
});
Ext.reg('apitreepanel', Docs.ApiTreePanel);

