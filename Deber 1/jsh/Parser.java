package jsh;

import java.util.ArrayList;
import java.util.List;

class Parser {
    static class Command {
        final String program;
        final List<String> args;
        final String inputFile;
        Command(String program, List<String> args, String inputFile) {
            this.program = program; this.args = args; this.inputFile = inputFile;
        }
        List<String> toCommandLineTokens() {
            List<String> list = new ArrayList<>();
            list.add(program);
            list.addAll(args);
            return list;
        }
        @Override public String toString(){
            StringBuilder sb = new StringBuilder();
            for (String t : toCommandLineTokens()) {
                if (sb.length()>0) sb.append(' ');
                sb.append(t);
            }
            if (inputFile != null) {
                sb.append(" < ").append(inputFile);
            }
            return sb.toString();
        }
    }

    static class Line {
        final List<Command> sequence;
        Line(List<Command> sequence) { this.sequence = sequence; }
        boolean isSequence() { return sequence.size() > 1; }
    }

    static Line parse(String raw) throws ParseException {
        List<String> seqParts = splitByBackgroundSeparator(raw);
        List<Command> sequence = new ArrayList<>();
        for (String part : seqParts) {
            sequence.add(parseSingle(part.trim()));
        }
        return new Line(sequence);
    }

    private static List<String> splitByBackgroundSeparator(String raw) throws ParseException {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (int i=0;i<raw.length();){
            if (raw.startsWith("&^", i)) {
                parts.add(cur.toString().trim());
                cur.setLength(0);
                i += 2;
            } else {
                char c = raw.charAt(i);
                if (c=='\'' || c=='\"') {
                    int[] res = readQuoted(raw, i);
                    cur.append(raw, i, res[1]);
                    i = res[1];
                } else {
                    cur.append(c);
                    i++;
                }
            }
        }
        String tail = cur.toString().trim();
        if (!tail.isEmpty()) parts.add(tail);
        return parts;
    }

    private static int[] readQuoted(String s, int start) throws ParseException {
        char q = s.charAt(start);
        int i = start + 1;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == q) {
                return new int[]{start, i+1};
            }
            i++;
        }
        throw new ParseException("Unterminated quote");
    }

    private static Command parseSingle(String part) throws ParseException {
        List<String> tokens = tokenize(part);
        String input = null;
        List<String> filtered = new ArrayList<>();
        for (int i=0;i<tokens.size();i++) {
            String t = tokens.get(i);
            if ("<".equals(t)) {
                if (i+1>=tokens.size()) throw new ParseException("Missing filename after <");
                input = tokens.get(++i);
            } else {
                filtered.add(t);
            }
        }
        if (filtered.isEmpty()) throw new ParseException("Empty command");
        String program = filtered.get(0);
        List<String> args = filtered.subList(1, filtered.size());
        return new Command(program, new ArrayList<>(args), input);
    }

    private static List<String> tokenize(String s) throws ParseException {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inSingle = false, inDouble = false;
        for (int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if (c=='\'' && !inDouble) { inSingle = !inSingle; continue; }
            if (c=='\"' && !inSingle) { inDouble = !inDouble; continue; }
            if (!inSingle && !inDouble && Character.isWhitespace(c)) {
                if (cur.length()>0) { out.add(cur.toString()); cur.setLength(0); }
                continue;
            }
            cur.append(c);
        }
        if (inSingle || inDouble) throw new ParseException("Unterminated quote");
        if (cur.length()>0) out.add(cur.toString());
        return out;
    }

    static class ParseException extends Exception {
        ParseException(String msg){ super(msg); }
    }
}
