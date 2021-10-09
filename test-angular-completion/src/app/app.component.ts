import { Component, ViewChild } from '@angular/core';

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
		public currVerbAutoCompletion: string|null,
		public complete: boolean
	) {
	}	

	static matchCommand(command: CommandInfo, parsedLine: ParsedLine): TextCommandMatch|null {
		let textVerbsCount = parsedLine.verbs.length;
		let commandVerbsCount = command.verbs.length;
		let maybeIncomplete = parsedLine.args.size == 0 && ! parsedLine.line.endsWith(' ');
		var i = 0;
		var currVerbAutoCompletion: string|null = null;
		for(; i < textVerbsCount; i++) {
			let textVerb = parsedLine.verbs[i];
			let commandVerb = command.verbs[i];
			let maybeLastIncomplete = maybeIncomplete && (i == commandVerbsCount-1);
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
		let complete = (i == command.verbs.length && 
			(null === currVerbAutoCompletion || '' === currVerbAutoCompletion)); // TOCHECK
		return new TextCommandMatch(command, i, currVerbAutoCompletion, complete);
	}

	static commonVerbAutoCompletionOf(matches: TextCommandMatch[]): string|null {
		var res: string|null = null;
		if (matches && matches.length!==0) {
			matches.forEach(match => {
				if (match.currVerbAutoCompletion) {
					res = VerbAlternative.mergeCommonPrefixOf(res, match.currVerbAutoCompletion);
				}
			});
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

  // @ViewChild('input1') input1;

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
	commandMatch: TextCommandMatch|null = null;


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

		// compute candidate command for autocompletions	
		let parsedLine = ParsedLine.parse(this.inputText);
		this.candidateCommandMatchs = this.listCandidateCommandMatchs(parsedLine);
		if (this.candidateCommandMatchs.length === 0) {
			this.commandMatch = null;
			this.currVerbAutoCompletion = null;
			// TODO unselect previous chars
		} else if (this.candidateCommandMatchs.length === 1) {
			// select all remaining chars
			this.commandMatch = this.candidateCommandMatchs[0];
			this.currVerbAutoCompletion = null;
		} else { // > 1
			// find common auto completion chars
			this.commandMatch = null;
			this.currVerbAutoCompletion = TextCommandMatch.commonVerbAutoCompletionOf(this.candidateCommandMatchs);
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
