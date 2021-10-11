import { Component, ViewChild, ElementRef } from '@angular/core';

class VerbAlternative {
	public readonly synonyms: Set<string>;
	constructor(public readonly text: string, 
		public readonly optSynonyms: string[]|null) {
		this.synonyms = new Set<string>();
		this.synonyms.add(text.toLocaleLowerCase());
		if(optSynonyms) {
			optSynonyms.forEach(x => this.synonyms.add(x.toLocaleLowerCase()));
		}
	}
	
	match(verb: string): boolean {
		return this.synonyms.has(verb);		
	}
	matchPrefixToComplete(verb: string): string|null {
		let res: string|null = null;
		this.synonyms.forEach(synonym => {
			if (synonym.startsWith(verb)) {
				let remain = synonym.substring(verb.length);
				res = VerbAlternative.mergeCommonPrefixOf(res, remain);
			}
		});
		return res;	
	}

	static mergeCommonPrefixOf(prev: string|null, text2: string): string {
		return (prev === null)? text2 : VerbAlternative.commonPrefixOf(prev, text2);
	}
	
	static commonPrefixOf(text1: string, text2: string): string {
		var i = 0;
		let len = Math.min(text1.length, text2.length);
		for(; i < len; i++) {
			if (text1.charAt(i) !== text2.charAt(i)) {
				break;
			}
		}
		return text1.substring(0, i);
	}
}

class CommandInfo {
	public readonly verbs: VerbAlternative[];
	constructor(
		public verb: string,
		public args: string[]) {
		this.verbs = verb.split(' ').map(x => new VerbAlternative(x, null));
	}
	
	remainVerbsText(from: number): string {
		let verbCount = this.verbs.length;
		if (from >= verbCount) {
			return '';
		}
		var res = this.verbs[from].text;
		for (var i = from+1; i < this.verbs.length; i++) {
			res += ' ' + this.verbs[i].text;
		}
		return res;
	}
}


class ParsedLine {
	constructor(public line: string, public verbs: string[], public args: Map<string,string>) {
	}

	static parse(text: string) {
		let parts = text.split(' ');
		let partsLen = parts.length;
		let verbs: string[] = [];
		let args = new Map<string,string>();
		var i = 0;
		for(; i < partsLen; i++) {
			let part = parts[i];
			if (-1 !== part.indexOf('=')) {
				break;
			}
			if ('' === part) { // trailing space
				break;
			}
			verbs.push(part.toLocaleLowerCase());
		}
		for(; i < partsLen; i++) {
			let part = parts[i];
			let sep = part.indexOf('=');
			if (-1 !== sep) {
				let arg = part.substring(0, sep); // .toLocaleLowerCase()
				let value = (sep < part.length)? part.substring(sep+1) : '';
				args.set(arg, value);			
			} else {
				// not a key-value arg?
				let arg = part; // .toLocaleLowerCase()
				args.set(arg, '');			
			}
		}
		return new ParsedLine(text, verbs, args);
	}
}

class TextCommandMatch {
	constructor(public command: CommandInfo,
		public currVerbIndex: number,
		public currVerbAutoCompletion: string|null, // deprecated
		public currAutoCompletion: string|null,
		public complete: boolean
	) {
	}	

	static matchCommand(command: CommandInfo, parsedLine: ParsedLine): TextCommandMatch|null {
		let textVerbsCount = parsedLine.verbs.length;
		let commandVerbsCount = command.verbs.length;
		if (textVerbsCount > commandVerbsCount) {
			return null;
		}
		let maybeIncomplete = parsedLine.args.size == 0 && ! parsedLine.line.endsWith(' ');
		var i = 0;
		var currVerbAutoCompletion: string|null = null;
		for(; i < textVerbsCount; i++) {
			let textVerb = parsedLine.verbs[i];
			let commandVerb = command.verbs[i];
			let maybeLastIncomplete = maybeIncomplete && (i == textVerbsCount-1);
			if (!maybeLastIncomplete) {
				if (! commandVerb.match(textVerb)) {
					return null;
				}
			} else {
				// test 
				let complete = commandVerb.matchPrefixToComplete(textVerb);
				if (null === complete) {
					return null
				}
				currVerbAutoCompletion = VerbAlternative.mergeCommonPrefixOf(currVerbAutoCompletion, complete);
			}
		}
		var currAutocompletion = (null !== currVerbAutoCompletion)? currVerbAutoCompletion : '';
		if (i < command.verbs.length) {
			if (currAutocompletion.length !== 0) {
				currAutocompletion += ' ';
			}
 			currAutocompletion += command.remainVerbsText(i);
		}

		let complete = (i == command.verbs.length && 
			(null === currVerbAutoCompletion || '' === currVerbAutoCompletion)); // TOCHECK
		return new TextCommandMatch(command, i, currVerbAutoCompletion, currAutocompletion, complete);
	}

// deprecated
	static commonVerbAutoCompletionOf(matches: TextCommandMatch[]): string|null {
		var res: string|null = null;
		if (matches && matches.length!==0) {
			for(var i = 0; i < matches.length; i++) {
				let match = matches[i];
				if (null === match.currVerbAutoCompletion) {
					return '';
				}
				if (match.currVerbAutoCompletion.length === 0) {
					return '';
				}
				res = VerbAlternative.mergeCommonPrefixOf(res, match.currVerbAutoCompletion);
			}
		}
		return res;
	}

	static commonAutoCompletionOf(matches: TextCommandMatch[]): string|null {
		var res: string|null = null;
		if (matches && matches.length !== 0) {
			for(var i = 0; i < matches.length; i++) {
				let match = matches[i];
				if (null === match.currAutoCompletion) {
					return '';	
				}
				if (match.currAutoCompletion.length === 0) {
					return '';
				}
				res = VerbAlternative.mergeCommonPrefixOf(res, match.currAutoCompletion);
			}
		}
		return res;
	}

}


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

	@ViewChild('input1', {static: true}) input1!: ElementRef;


  title = 'test-angular-completion';
	verboseLog = false;
	
	inputText: string = "add user ";

	commands: CommandInfo[] = [
		new CommandInfo('add user to project', [ 'user', 'project']),
		new CommandInfo('add user to projects', [ 'user', 'projects']),
		new CommandInfo('add user to group', [ 'user', 'group']),
		new CommandInfo('add user to group sub-group1', [ 'user', 'group', 'subgroup1']),
		new CommandInfo('add user to group sub-group2', [ 'user', 'group', 'subgroup2']),
		
	];	
	candidateCommandMatchs: TextCommandMatch[] = [];
	currVerbAutoCompletion: string|null = null;
	currAutoCompletion: string|null = null;
	commandMatch: TextCommandMatch|null = null;

	constructor(input1: ElementRef){
    	this.input1 = input1;
	}


	onInputBlur() {
		if (this.verboseLog) console.log('onInputBlur')	
	}
	onInputFocus() {
		if (this.verboseLog) console.log('onInputFocus')	
	}
	onInputChange() {
		if (this.verboseLog) console.log('onInputChange')	
		
	}
	onInputKeyUp(event: any) {
		if (this.verboseLog) console.log('onInputKeyUp ', event)	
		// TODO if <ctrl>+' ' => apply autocompletion proposal

		let input1 = <HTMLInputElement> this.input1.nativeElement;
		let currText = <string> input1.value;
		let currTextLen = currText.length;

		// do not propose autocompletion if caret is not at end of input
		let selectStart = input1.selectionStart;
		let selectEnd = input1.selectionEnd;
		if (selectStart !== selectEnd || selectStart !== currTextLen) {
			return;
		}	
		
		if (this.inputText !== currText) {
			// console.log('onKeyUp, detected this.inputText !== currText:', [this.inputText,  currText]);			
		}
		
		// compute candidate command for autocompletions	
		let parsedLine = ParsedLine.parse(currText);
		this.candidateCommandMatchs = this.listCandidateCommandMatchs(parsedLine);
		if (this.candidateCommandMatchs.length === 0) {
			this.commandMatch = null;
			this.currVerbAutoCompletion = null; // deprecated
			this.currAutoCompletion = null;
			// TODO unselect previous chars
		} else if (this.candidateCommandMatchs.length === 1) {
			// select all remaining chars
			this.commandMatch = this.candidateCommandMatchs[0];
			this.currVerbAutoCompletion = this.commandMatch.currVerbAutoCompletion; // deprecated
			this.currAutoCompletion = this.commandMatch.currAutoCompletion;
		} else { // > 1
			// find common auto completion chars
			this.commandMatch = null;
			this.currVerbAutoCompletion = TextCommandMatch.commonVerbAutoCompletionOf(this.candidateCommandMatchs); // deprecated
			this.currAutoCompletion = TextCommandMatch.commonAutoCompletionOf(this.candidateCommandMatchs);
		}
		
		if (null != this.currAutoCompletion && '' !== this.currAutoCompletion) {
			if (currText.endsWith(this.currAutoCompletion)) {
				return;
			}
			console.log('currText:"' + currText + '" len' + currTextLen + " selectStart:" + selectStart + '  => insert "' + this.currAutoCompletion + '"');
			input1.setRangeText(this.currAutoCompletion, currTextLen, currTextLen, 'select');
		}
		
	}
	
	listCandidateCommandMatchs(parsedLine: ParsedLine): TextCommandMatch[] {
		let res: TextCommandMatch[] = []; 
		this.commands.forEach(c => {
			let match = TextCommandMatch.matchCommand(c, parsedLine);
			if (match) {
				res.push(match);	
			}
		});
		return res;
	}


}
