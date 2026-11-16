# FleetHub report template contract

## Reference and verification

- Reference: `C:\Users\MVR TARUN\Downloads\Aoop_aq2_Report Format.docx`
- SHA-256: `21219a04e6c99fa11b30ce59e0788fc3b91cdb276fb2614794870451d1c82a9a`
- Package size: 347,260 bytes
- Cached page count in `docProps/app.xml`: 16 (not rendered in this environment)
- Sections: 4, all US Letter portrait
- Read-only evidence: `_report_work/template-style-evidence.json`; section audit output from the task log; extracted media in `_report_work/template_media/`
- Render attempt: `render_docx.py` could not start because bundled LibreOffice is absent and `soffice.exe` is not on PATH. Verbose retry confirmed the same missing executable. Page count and visual reference remain unresolved.

## Page system

- Section 1: continuous, 8.5 x 11 in; margins L 1.028, R 0.917, T 0.944, B 0.194 in.
- Section 2: new page, 8.5 x 11 in; margins L 1.028, R 0.917, T 1.042, B 0.500 in.
- Section 3: new page, 8.5 x 11 in; margins L 1.028, R 0.917, T 0.486, B 0.500 in.
- Section 4: new page, 8.5 x 11 in; margins L 1.028, R 0.917, T 0.444, B 0.500 in.
- Headers and footers contain no visible text in paragraph extraction; preserve all header/footer parts and relationships.
- The template has no TOC or page-number field codes in `word/document.xml`; its contents table is static and its page-number cells are blank. Keep page-number cells blank rather than invent pagination.

## Typography, tables, and recurring components

- Preserve the source's Times New Roman body style and direct formatting. `Normal` resolves to Times New Roman. `Heading 1` is 13 pt bold; many paragraphs also carry direct formatting, so source paragraphs should be edited in place.
- Preserve the source's paragraph spacing, tab stops, section boundaries, and numbering definitions. Do not normalize styles globally.
- Three `Table Grid` tables: signature block (2 columns; approx. 3.052/2.756 in), contents (3 columns; approx. 1.344/3.445/1.280 in), and data table (3 columns; approx. 2.083/2.076/2.094 in). Preserve table geometry and existing signature rows.
- Image map: `image1.jpeg` is the SRM Institute logo on the cover and must remain unchanged. `image2.png` is the network topology in the architecture chapter; `image3.png` and `image4.png` are Packet Tracer ping screenshots; `image5.png` is a Packet Tracer browser screenshot. Replace only images 2-5 with FleetHub architecture and workflow diagrams while preserving their package paths, relationships, and inline drawing extents.
- Main body sections, in source order: cover; bonafide certificate; abstract; acknowledgement; table of contents; chapters 1-8; references.

## Editable slot map

- Cover title: paragraph 0. Replace with `FLEETHUB MANAGEMENT`.
- Course label: paragraph 14. Keep the course/institution metadata from the supplied report format.
- Team list: paragraphs 7-9. Replace both previous students with the three user-provided members and IDs; reuse blank paragraph 9 with copied member-line formatting.
- Guidance line: paragraph 11. Keep the guide named in the supplied template (Dr. S. Srividhya); do not invent another faculty name.
- Date: paragraph 27. Use November 2026, matching the supplied template's date and the current report date.
- Certificate: paragraph 35. Replace project title and team identities; preserve signature table, signatures, institutional logo, and certificate layout.
- Abstract: paragraph 62. Summarize the Java Swing + MySQL fleet lifecycle system, its DAO architecture, core workflows, and verification status without claiming a successful live database connection.
- Acknowledgement: paragraphs 67-74. Replace unrelated personal names/text; acknowledge the project guide, department/institution, and general support without attributing unsupported roles.
- Contents: table 1. Retain the 8-chapter hierarchy; update chapter labels to match this report; leave page numbers blank because the template uses static blank cells and pagination is unresolved.
- Introduction: paragraph 97 is the chapter heading; use nearby blank/body paragraphs for motivation, objectives, problem statement, and scope/challenges.
- Literature survey: paragraph 126 is the heading; populate a concise technology survey in its existing paragraph region, with citations `[1]`-`[5]`.
- Requirements: paragraph 161 is the chapter heading; paragraphs 163-203 contain the analysis/list slots. Replace network hardware content with functional/nonfunctional/software/data requirements.
- Architecture: paragraph 228 is the chapter heading; paragraph 230 is the subheading, paragraph 233 holds image 2. Replace associated text with the UI/DAO/JDBC/MySQL architecture and caption.
- Implementation: paragraph 250 is the chapter heading; paragraph 252 and following paragraphs introduce the database schema, configuration, transactions, authentication, rental/return processes, reports, and audit logs. Table 2 is the schema summary.
- Results: paragraph 274 is the chapter heading; paragraphs 276-303 and images 3-5 hold verification/workflow figures and captions. Report the verified compile and clearly state that the live DB connection was not established during this task.
- Conclusion: paragraph 314 is the chapter heading; fill existing following paragraph slots with conclusions, limits, and future improvements.
- References: paragraph 359 is the heading; paragraphs 360-361 contain primary Java and MySQL documentation references.

## Package preservation and fidelity gates

- Keep the source DOCX byte-for-byte unchanged.
- Preserve `word/styles.xml`, `word/numbering.xml`, section properties, `word/footer1.xml`, `word/footer2.xml`, `word/theme/theme1.xml`, `word/settings.xml`, `word/fontTable.xml`, custom XML, and every relationship unless an explicitly mapped edit requires otherwise.
- Intended package edits: `word/document.xml`, its existing PNG media assets for images 2-5, and `docProps/core.xml` metadata. Keep `[Content_Types].xml`, relationships, and all other package parts byte-identical.
- Target output: `D:\AOPD Project\FleetHub_Management_Report.docx`.
- Fidelity deviation: no bundled LibreOffice is available for the required `render_docx.py` render. Use Microsoft Word's existing renderer for visual inspection if accessible; do not silently substitute a different template style. Record the remaining renderer limitation if Word visual QA cannot be completed.
