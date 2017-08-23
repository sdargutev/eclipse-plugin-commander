# Eclipse Commander
Provides command palette [KAVI](https://github.com/dakaraphi/kavi) interfaces exceeding capabilities of typical sublime like command palettes

Current interfaces provided:
 * **Commander** - provides interface for selecting and executing all Eclipse commands.
 * **Finder** - provides interface for selecting and opening workspace resources.

# Background
## The problem
* In a very large complex application such as Eclipse, your workflow can be significantly slowed due to the massive complexity of the application.
* Eclipse environments can easily have well over 2000 commands which are executed through menus, hot keys, custom dialogs or buttons.
* Remembering where are the locations of menus, views, buttons or attempting to assign so many actions to hot keys proves to be impractical to impossible.
* Think time increases simply attempting to navigate and execute your intentions.
* Quick Access is the Eclipse attempt to address these issues, but fails to be an optimal implementation of a solution.
    * Modern fuzzy matching like sublime is not implemented requiring additional keystrokes 
    * Ranking is not optimal, partially due also to group sorting of actions by category which requires navigation further down the list
    * Reuse of recent actions is not implemented in an intuitive way
    * Not designed with fast keyboard interaction in mind

## Goals
* Provide fastest workflow possible in a complex application
    * Interface should be faster than using mouse, menus, buttons, or even hot keys in most cases.
    * Typing flow should never be interrupted.
    * All actions should be possible without use of mouse.
    * Common used actions should have quicker access.

# KAVI implemented features for all interfaces 
 
## Term Matching

### Multi column
Default match will search across columns for matches.
![multi column](/readme-images/multi-column-match.gif)
### Specific column
Columns can be selected individual for matches by using a `,` to separate the column filters.
![specific column](/readme-images/specific-column-match.gif)
### Literal 
A space preceding filter text will cause the following text to be matched literal instead of fuzzy.
![literal column](/readme-images/literal-match.gif) 
### Fuzzy multi word out of order
Contiguous characters matched using a fuzzy strategy that attempts to match words in any order.
A space separating words will force matching of the literal words also allowing for out of order matces.
![fuzzy column](/readme-images/fuzzy-out-of-order-match.gif)
### Quality filtering
One or two letters will not match in the middle of words.  This is done to prevent a long tail of low ranking matches.
![quality](/readme-images/quality-match.gif)
### Acronym
Fuzzy matching also will attempt to match by acronym
![acronym column](/readme-images/acronym-match.gif)
## Ranking Sort
Items are sorted first by rank and then by name.
This allows for grouping of items by rank and easier identification of items within the ranked group.

![acronym column](/readme-images/ranking-sort.gif)

## Fast Select
Fast select allows list actions directly on target items without needing to navigate to the item with mouse or keyboard cursor.
This mode is enabled when typing `/` in the filter input.

### Immediate action invocation
With `Fast Select` enabled, typing the letters in the fast select guide next to the row immediately inititates that row action.
![acronym column](/readme-images/fast-select.gif)
### Multi select
`Fast Multi Select` allows fast selection of multiple items.  This mode is active when `//` is entered in the input field.

### Range select
A range can be selected by starting the row identifier with `-`.  The range will be applied from the last selected item and will use the selected state of the that same item.

### Inverse select
Inverse select will inverse all selections currently in the filtered view.  
Inverse select is performed by pressing `!` after the fast select slashes `//`

### All select/deselect
If any items in the view are selected, this action will deselect all selected.  Otherwise, this same action will select all in the filtered view.
`All select/deselect` is performed by pressing `space` after the fast select slashses `//`

### Implied selections
Some actions will always use the first item in the list if there is no selection or cursored item.
Context actions will perform actions on all items of the previous view of there is nothing selected.

## Navigation
### List paging
`crtl+j` will page down in the list.
`ctrl+k` will page up in the list.

### Cursor movement in input field
`ctrl+j` will move to beginning of input field
`ctrl+l` will move to the end of the input field

## Working and Discovery Modes
`Working` is a view of the set of items that consist of favorites and or recently used items.
This view is intended to be the primary view that you would use most of the time and therefore is the default view.
However, this view does need to be primed before it is useful.  Over the course of a few days this view would accumulate actions or items you are currently using.

`Discovery` is a view of all possible items.  Selection of items from Discovery will add them to the recent list which makes them appear in the `Working` view.

### Switching modes
Press `TAB` to instantly switch view modes between `Working` and `Discovery`

### Recent
Recent items are shown in the `Working` view.  The items are always sorted by most recent.  To reuse the last used item, simply open the dialog and press enter which will default to using the first item in the list.

### Favorites
Items can also be permanently added to the `Working` view.  These are considered favorite items.  They are also sorted in by most recently used in the same view as recent items.

### Export/Import preferences
The `Working` set of items is contained within preferences and will be exported and imported with Eclipse preferences.

### Across workspaces
The `Working` set of items is stored in the global preferences store.  Therefore, your recently used commands will still be available across workspaces.

## Context Actions
Context actions are those actions that otherwise would require right clicking on an item to bring up another menu or dialog.
Context actions here are initiated using the `;` key.
The context actions will be performed on all selected items from the previous view.

### View Selected
This action will toggle showing only the selected items in the view.  This allows you to type different input filters, select items and then finally see all the items you have selected at once before performing some action on those items.

### Favorites
Favorites can be added or removed through context actions.

### Sort
Items which are normally sorted by rank or sorted by most recent can be sorted by name using this context action.

# Commander
## Columns
## Launcher
# Finder
## Columns
##
