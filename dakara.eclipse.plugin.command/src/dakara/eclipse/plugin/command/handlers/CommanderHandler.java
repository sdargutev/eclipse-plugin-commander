package dakara.eclipse.plugin.command.handlers;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.lang.model.element.Element;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.quickaccess.QuickAccessElement;

import dakara.eclipse.plugin.command.Constants;
import dakara.eclipse.plugin.command.eclipse.internal.EclipseCommandProvider;
import dakara.eclipse.plugin.command.settings.CommandDialogPersistedSettings;
import dakara.eclipse.plugin.command.settings.CommandDialogPersistedSettings.HistoryKey;
import dakara.eclipse.plugin.kavi.picklist.InternalCommandContextProvider;
import dakara.eclipse.plugin.kavi.picklist.InternalContentProviderProxy;
import dakara.eclipse.plugin.kavi.picklist.KaviPickListDialog;
import dakara.eclipse.plugin.stringscore.FieldResolver;
import dakara.eclipse.plugin.stringscore.ListRankAndFilter;


@SuppressWarnings("restriction")
public class CommanderHandler extends AbstractHandler {
	private EclipseCommandProvider eclipseCommandProvider;
	private KaviPickListDialog<QuickAccessElement> kaviPickList;

	/* TODO's
	 * - allow other commands to reuse dialog to show other lists for faster speed
	 * - need ability to issue internal commands on lists
	 *   - for example, need command to remove an item from history
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		initialize(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell().getDisplay());
		return null;
	}
	
	public void initialize(Display display) throws ExecutionException {
		FieldResolver<QuickAccessElement> providerField = new FieldResolver<>("provider",  item -> item.getProvider().getName());
		FieldResolver<QuickAccessElement> labelField = new FieldResolver<>("label",  item -> item.getLabel());
		ListRankAndFilter<QuickAccessElement> listRankAndFilter = CommanderContentProviders.listRankAndFilter(labelField, providerField);
		
		eclipseCommandProvider = new EclipseCommandProvider();
		CommandDialogPersistedSettings<QuickAccessElement> historyStore = createSettingsStore(display, eclipseCommandProvider);
		
		kaviPickList = new KaviPickListDialog<>();
		kaviPickList.setListContentProvider("discovery", CommanderContentProviders.listContentDiscoveryProvider(listRankAndFilter, historyStore, eclipseCommandProvider))
					.setResolvedAction(resolvedAction(display, historyStore))
					.addColumn(labelField.fieldId, labelField.fieldResolver).widthPercent(100)
					.addColumn(providerField.fieldId, providerField.fieldResolver).width(85).right().italic().fontColor(100, 100, 100).backgroundColor(250, 250, 250);
		
		kaviPickList.setListContentProvider("recall",    CommanderContentProviders.listContentRecallProvider(listRankAndFilter, historyStore, eclipseCommandProvider))
					.setResolvedAction(resolvedAction(display, historyStore))
					.addColumn(labelField.fieldId, labelField.fieldResolver).widthPercent(100)
					.addColumn(providerField.fieldId, providerField.fieldResolver).width(85).right().italic().fontColor(100, 100, 100).backgroundColor(250, 250, 250);
		
		
		InternalCommandContextProvider contextProvider = new InternalCommandContextProvider();
		contextProvider.addCommand("discovery", "list: toggle view selected", (InternalContentProviderProxy<QuickAccessElement> provider) -> provider.toggleViewOnlySelected());
		contextProvider.addCommand("discovery", "history: remove", (InternalContentProviderProxy<QuickAccessElement> provider) -> {
			provider.getSelectedEntries().stream().map(item -> item.dataItem).forEach(item -> historyStore.removeHistory(item));
			historyStore.saveSettings();
		});
		contextProvider.addCommand("discovery", "history: keep", (InternalContentProviderProxy<QuickAccessElement> provider) -> {
			provider.getSelectedEntries().stream().map(item -> item.dataItem).forEach(item -> historyStore.setHistoryPermanent(item, true));
			historyStore.saveSettings();
		});
		
		kaviPickList.setListContentProvider("_internal", contextProvider.makeProviderFunction()).setRestoreFilterTextOnProviderChange(true)
		            .setResolvedContextAction((command, provider) -> command.handleSelections.accept(provider)) // get previous provider selections
		            .addColumn("name", item -> item.name).widthPercent(100);
		
		kaviPickList.setBounds(600, 400);
		kaviPickList.setCurrentProvider("recall");
		kaviPickList.open();	
	}

	private CommandDialogPersistedSettings<QuickAccessElement> createSettingsStore(Display display, EclipseCommandProvider eclipseCommandProvider) {
		Function<HistoryKey, QuickAccessElement> historyItemResolver = historyKey -> eclipseCommandProvider.getCommand(historyKey.keys.get(0), historyKey.keys.get(1));
		CommandDialogPersistedSettings<QuickAccessElement> historyStore = new CommandDialogPersistedSettings<>(Constants.BUNDLE_ID, 100, item -> new HistoryKey(item.getProvider().getId(), item.getId()), historyItemResolver);
		historyStore.loadSettings();
		
		return historyStore;
	}

	private Consumer<QuickAccessElement> resolvedAction(Display display, CommandDialogPersistedSettings<QuickAccessElement> historyStore) {
		return (item) -> {
			display.asyncExec(item::execute);
			historyStore.addToHistory(item);
			historyStore.saveSettings();
		};
	}	
}
