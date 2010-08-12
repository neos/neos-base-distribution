Ext.ns('Docs');



Docs.ApiTabPanel = Ext.extend( Ext.TabPanel, {
    constructor : function(cfg){

        this.searchStore = this.buildSearchStore();
        cfg = Ext.applyIf(cfg || {}, {
            minTabWidth: 135,
            tabWidth: 135,
            enableTabScroll: true,
            activeTab: 0,
            resizeTabs: true,
            items   : this.buildItems(),
            plugins : [
                new Ext.ux.TabCloseMenu(),
                new Ext.ux.TabScrollerMenu({
                    maxText  : 15,
                    pageSize : 5
                })
            ]
        });
        
        Docs.ApiTabPanel.superclass.constructor.call(this, cfg);
    },
    buildItems : function() {
        return {
            title: 'API Home',
            iconCls:'icon-docs',
            autoScroll: true,
            autoLoad: {
                url: 'welcome.html',
                callback: this.onWelcomeLoadInitSearch,
                scope: this
            },
            tbar: [
                'Search: ', ' ',
                new Ext.ux.SelectBox({
                    listClass:'x-combo-list-small',
                    width:90,
                    value:'Starts with',
                    itemId:'search-type',
                    store: new Ext.data.SimpleStore({
                        fields: ['text'],
                        expandData: true,
                        data : ['Starts with', 'Ends with', 'Any match']
                    }),
                    displayField: 'text'
                }), ' ',
                new Ext.app.SearchField({
                    width:240,
                    store: this.searchStore,
                    paramName: 'q'
                })
            ]
        };
    },
    initEvents : function(){
        Docs.ApiTabPanel.superclass.initEvents.call(this);
        this.body.on('click', this.onBodyClick, this);
    },
    buildSearchStore : function() {
        return new Ext.data.Store({
            baseParams: {},
            proxy: new Ext.data.ScriptTagProxy({
                url: 'http://extjs.com/playpen/api.php'
            }),
            reader: new Ext.data.JsonReader({
                    root: 'data'
                },
                ['cls', 'member', 'type', 'doc']
            ),
            listeners: {
                scope      : this,
                beforeload : this.onSearchStoreBeforeLoad
            }
        });     
    },
    onBodyClick: function(e, target){
        if(target = e.getTarget('a:not(.exi)', 3)){
            var cls = Ext.fly(target).getAttributeNS('ext', 'cls');
            e.stopEvent();
            if(cls){
                var member = Ext.fly(target).getAttributeNS('ext', 'member');
                this.loadClass(target.href, cls, member);
            }
            else if(target.className == 'inner-link'){
                this.getActiveTab().scrollToSection(target.href.split('#')[1]);
            }
            else{
                window.open(target.href);
            }
        }
        else if(target = e.getTarget('.micon', 2)){
            e.stopEvent();
            var tr = Ext.fly(target.parentNode);
            if(tr.hasClass('expandable')){
                tr.toggleClass('expanded');
            }
        }
    },

    loadClass : function(href, cls, member){
        var id  = 'docs-' + cls,
            tab = this.getComponent(id);
        if(tab){
            this.setActiveTab(tab);
            if(member){
                tab.scrollToMember(member);
            }
        }
        else{
            var autoLoad = {url: href};
            if(member){
                autoLoad.scope = this;
                autoLoad.callback = function(){
                    this.getComponent(id).scrollToMember(member);
                }
            }
            var p = this.add({
                xtype : 'docpanel',
                itemId : id,
                cclass : cls,
                autoLoad: autoLoad,
                iconCls: Docs.icons[cls]
            });
            this.setActiveTab(p);
        }
    },
	
	onWelcomeLoadInitSearch : function(){
		// Custom rendering Template for the View
	    var resultTpl = new Ext.XTemplate(
	        '<tpl for=".">',
	        '<div class="search-item">',
	            '<a class="member" ext:cls="{cls}" ext:member="{member}" href="output/{cls}.html">',
				'<img src="resources/images/default/s.gif" class="item-icon icon-{type}"/>{member}',
				'</a> ',
				'<a class="cls" ext:cls="{cls}" href="output/{cls}.html">{cls}</a>',
	            '<p>{doc}</p>',
	        '</div></tpl>',
            {
                compile : true
            }
	    );
		
		new Ext.DataView({
            applyTo: 'search',
			tpl: resultTpl,
			loadingText:'Searching...',
            store: this.searchStore,
            itemSelector: 'div.search-item',
			emptyText: '<h3>Use the search field above to search the Ext API for classes, properties, config options, methods and events.</h3>'
        });
	},
	
	doSearch : function(e){
		var k = e.getKey();
		if(!e.isSpecialKey()){
			var text = e.target.value;
			if(!text){
				this.searchStore.baseParams.q = '';
				this.searchStore.removeAll();
			}else{
				this.searchStore.baseParams.q = text;
				this.searchStore.reload();
			}
		}
	},
    onSearchStoreBeforeLoad : function(store) {
        store.baseParams.qt = this.getTopToolbar().getComponent('search-type').getValue();
    }
});

Ext.reg('apitabpanel', Docs.ApiTabPanel);
