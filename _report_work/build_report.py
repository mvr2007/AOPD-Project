from copy import deepcopy
from hashlib import sha256
from io import BytesIO
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile
import shutil

from docx import Document
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(r"D:\AOPD Project")
WORK = ROOT / "_report_work"
TEMPLATE = Path(r"C:\Users\MVR TARUN\Downloads\Aoop_aq2_Report Format.docx")
OUTPUT = ROOT / "FleetHub_Management_Report.docx"
TEMP_DOCX = WORK / "docx_working_copy.docx"
TMP_ZIP = WORK / "final_package.docx"

CREAM = "#FBF8F1"
PANEL = "#F5EEE6"
SAGE = "#88A47C"
TERRA = "#E27355"
SLATE = "#2C3531"
GREEN = "#2E7D32"
AMBER = "#D99A2B"
INK = "#28332E"
MUTED = "#68736B"
WHITE = "#FFFFFF"


def font(size, bold=False):
    candidates = [
        Path(r"C:\Windows\Fonts\segoeuib.ttf" if bold else r"C:\Windows\Fonts\segoeui.ttf"),
        Path(r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf"),
    ]
    for path in candidates:
        if path.exists():
            return ImageFont.truetype(str(path), size)
    return ImageFont.load_default()


def rounded(draw, box, fill, radius=18, outline=None, width=1):
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=width)


def wrap_text(draw, text, ft, max_width):
    words = text.split()
    lines, current = [], ""
    for word in words:
        candidate = f"{current} {word}".strip()
        if current and draw.textbbox((0, 0), candidate, font=ft)[2] > max_width:
            lines.append(current)
            current = word
        else:
            current = candidate
    if current:
        lines.append(current)
    return lines


def paragraph(draw, text, x, y, ft, color, max_width, line_gap=7):
    lines = wrap_text(draw, text, ft, max_width)
    line_height = draw.textbbox((0, 0), "Ag", font=ft)[3] + line_gap
    for line in lines:
        draw.text((x, y), line, font=ft, fill=color)
        y += line_height
    return y


def header(draw, width, title, subtitle=None):
    draw.rectangle((0, 0, width, 132), fill=SLATE)
    draw.text((42, 30), title, font=font(36, True), fill=WHITE)
    if subtitle:
        draw.text((44, 82), subtitle, font=font(18), fill="#D5DED6")


def architecture_diagram(path):
    im = Image.new("RGB", (1920, 1080), CREAM)
    d = ImageDraw.Draw(im)
    header(d, 1920, "FLEETHUB SYSTEM ARCHITECTURE", "Desktop client  |  Java Swing  |  JDBC  |  MySQL")
    nodes = [
        (55, 245, 430, 700, "PRESENTATION", SAGE, ["Fleet Inventory", "Rent a Vehicle", "Active & Overdue", "Customers & History", "Business Analytics", "Audit Log"]),
        (525, 245, 900, 700, "APPLICATION / DAO", TERRA, ["FleetDAO", "PreparedStatement queries", "ACID checkout / return", "Input and status checks", "Revenue and CSV reports"]),
        (995, 245, 1370, 700, "CONNECTIVITY", AMBER, ["DBConnection", "config.properties", "MySQL Connector/J", "JDBC connection", "Commit / rollback"]),
        (1465, 245, 1865, 700, "MYSQL DATA", GREEN, ["vehicles", "customers", "rentals", "users", "audit_logs"]),
    ]
    for x1, y1, x2, y2, title, color, lines in nodes:
        rounded(d, (x1, y1, x2, y2), WHITE, 24, outline="#DDD5C9", width=2)
        rounded(d, (x1, y1, x2, y1 + 76), color, 22)
        d.rectangle((x1, y1 + 52, x2, y1 + 76), fill=color)
        d.text((x1 + 24, y1 + 23), title, font=font(23, True), fill=WHITE)
        yy = y1 + 112
        for line in lines:
            d.ellipse((x1 + 24, yy + 8, x1 + 37, yy + 21), fill=color)
            yy = paragraph(d, line, x1 + 52, yy, font(21), INK, x2 - x1 - 78, 8) + 22
    for x in [440, 910, 1380]:
        d.line((x, 470, x + 75, 470), fill=SLATE, width=8)
        d.polygon([(x + 75, 456), (x + 99, 470), (x + 75, 484)], fill=SLATE)
    rounded(d, (150, 790, 1770, 950), PANEL, 20)
    d.text((192, 824), "REQUEST", font=font(21, True), fill=SLATE)
    d.text((395, 824), "validate", font=font(21, True), fill=SAGE)
    d.text((620, 824), "execute transaction", font=font(21, True), fill=TERRA)
    d.text((1065, 824), "commit on success", font=font(21, True), fill=GREEN)
    d.text((1430, 824), "rollback on failure", font=font(21, True), fill=TERRA)
    d.text((192, 880), "User actions pass through the DAO; the database stores operational records and the audit trail.", font=font(22), fill=MUTED)
    im.save(path)


def square_flow(path, title, subtitle, steps, footer, accent):
    im = Image.new("RGB", (700, 690), CREAM)
    d = ImageDraw.Draw(im)
    header(d, 700, title, subtitle)
    y = 158
    for i, (label, detail) in enumerate(steps, 1):
        rounded(d, (34, y, 666, y + 90), WHITE, 18, outline="#DED6CB", width=2)
        d.ellipse((50, y + 21, 98, y + 69), fill=accent)
        n = str(i)
        box = d.textbbox((0, 0), n, font=font(22, True))
        d.text((74 - (box[2] - box[0]) / 2, y + 30), n, font=font(22, True), fill=WHITE)
        d.text((116, y + 12), label, font=font(20, True), fill=SLATE)
        paragraph(d, detail, 116, y + 43, font(15), MUTED, 510, 4)
        y += 104
    rounded(d, (34, 582, 666, 658), PANEL, 16)
    paragraph(d, footer, 52, 598, font(17, True), SLATE, 585, 4)
    im.save(path)


def dashboard_map(path):
    im = Image.new("RGB", (701, 686), CREAM)
    d = ImageDraw.Draw(im)
    header(d, 701, "FLEETHUB MANAGEMENT", "Lifecycle console  |  role-aware desktop workspace")
    # KPI strip
    kpis = [("TOTAL FLEET", SAGE), ("AVAILABLE", GREEN), ("ACTIVE LEASES", TERRA), ("REVENUE", SLATE)]
    w = 150
    for i, (label, color) in enumerate(kpis):
        x = 22 + i * 164
        rounded(d, (x, 155, x + w, 238), WHITE, 14, outline="#DED6CB")
        d.rounded_rectangle((x, 155, x + 7, 238), radius=4, fill=color)
        d.text((x + 17, 174), label, font=font(11, True), fill=MUTED)
        d.text((x + 17, 202), "Live KPI", font=font(18, True), fill=SLATE)
    d.text((28, 265), "SIX WORKSPACES", font=font(17, True), fill=SLATE)
    views = [
        ("01", "Fleet Inventory", "Search, filter, maintain and price vehicles"),
        ("02", "Rent a Vehicle", "Customer, vehicle, duration and invoice estimate"),
        ("03", "Active & Overdue Rentals", "Expected return dates and odometer-based return"),
        ("04", "Customers & History", "Customer records and previous rentals"),
        ("05", "Business Analytics", "Category revenue and fleet utilization"),
        ("06", "System Audit Log", "Recent user actions and timestamps"),
    ]
    positions = [(24, 300), (363, 300), (24, 388), (363, 388), (24, 476), (363, 476)]
    for (num, name, detail), (x, y) in zip(views, positions):
        rounded(d, (x, y, x + 312, y + 74), WHITE, 14, outline="#DED6CB")
        d.text((x + 14, y + 15), num, font=font(17, True), fill=SAGE)
        d.text((x + 50, y + 11), name, font=font(15, True), fill=SLATE)
        paragraph(d, detail, x + 50, y + 37, font(11), MUTED, 248, 2)
    rounded(d, (24, 576, 677, 654), SLATE, 15)
    d.text((43, 591), "ACCESS", font=font(13, True), fill="#D5DED6")
    d.text((43, 615), "ADMIN manages tariffs  ·  STAFF handles daily fleet operations", font=font(14, True), fill=WHITE)
    im.save(path)


def replace_text(paragraph, text, format_from=None):
    source = format_from or paragraph
    source_run = source.runs[0] if source.runs else None
    source_rpr = deepcopy(source_run._r.rPr) if source_run is not None and source_run._r.rPr is not None else None
    if format_from is not None:
        paragraph.style = format_from.style
        if format_from._p.pPr is not None:
            if paragraph._p.pPr is not None:
                paragraph._p.remove(paragraph._p.pPr)
            paragraph._p.insert(0, deepcopy(format_from._p.pPr))
    paragraph.clear()
    if text:
        run = paragraph.add_run(text)
        if source_rpr is not None:
            run._r.insert(0, source_rpr)


def set_cell(cell, text):
    p = cell.paragraphs[0]
    replace_text(p, text)
    for extra in cell.paragraphs[1:]:
        replace_text(extra, "")


def make_content():
    doc = Document(str(TEMPLATE))
    p = doc.paragraphs
    edits = {
        0: "FLEETHUB MANAGEMENT",
        2: "MINOR PROJECT REPORT",
        4: "By",
        7: "MVR TARUN (RA2511027010046)",
        8: "PRANAVA SAHITH (RA2511027010078)",
        11: "Under the guidance of\nDr. S. Srividhya\nIn partial fulfilment of the Course",
        12: "of",
        14: "21CSC206P - ADVANCED OBJECT ORIENTED AND PROGRAMMING",
        16: "In",
        17: "Department of Data Science and Business Systems",
        27: "NOVEMBER 2026",
        32: "BONAFIDE CERTIFICATE",
        35: ("Certified that this minor project report for the course 21CSC206P ADVANCED OBJECT ORIENTED AND PROGRAMMING, "
             "entitled \"FleetHub Management\", is the bonafide work of MVR TARUN (RA2511027010046), "
             "PRANAVA SAHITH (RA2511027010078), and SOURADEEP PAUL (RA2511027010098), carried out under the guidance of Dr. S. Srividhya."),
        59: "ABSTRACT",
        62: ("FleetHub Management is a Java Swing desktop application backed by MySQL for coordinating vehicle inventory, "
             "customers, rentals, returns, service status, and business reporting. The interface organizes operations into six views: "
             "Fleet Inventory, Rent a Vehicle, Active and Overdue Rentals, Customers and History, Business Analytics, and System Audit Log. "
             "The FleetDAO layer separates database operations from the interface and uses prepared statements for parameterized queries. "
             "Checkout records the customer, vehicle, rental period, expected return, discount, tax, and starting odometer while changing "
             "vehicle availability in the same transaction. Return processing records the ending odometer, calculates an overdue fee when "
             "applicable, completes the rental, and updates the vehicle's odometer and service status. The report also covers role-based "
             "ADMIN and STAFF access, audit events, customer rental history, category revenue, CSV export, and the additive schema migration. "
             "The Java sources compiled successfully. A live Java-to-MySQL integration check was not confirmed during report preparation; "
             "the configured JDBC connection must be verified against the local MySQL credentials before runtime results are treated as complete."),
        65: "ACKNOWLEDGEMENT",
        67: ("We express our sincere gratitude to our project guide, Dr. S. Srividhya, for her guidance, timely suggestions, "
             "and support throughout the development of FleetHub Management."),
        68: ("We thank the Department of Data Science and Business Systems, the School of Computing, and SRM Institute of Science and Technology "
             "for the academic environment and facilities provided for this course project."),
        69: ("We also thank our classmates, friends, and families for their encouragement during the planning, implementation, and documentation "
             "of the application."),
        70: "",
        71: "",
        72: "",
        73: "",
        74: "",
        77: "TABLE OF CONTENTS",
        97: "INTRODUCTION",
        99: "1.1 Motivation",
        101: ("Vehicle rental teams must coordinate fleet availability, customer records, rental terms, and vehicle returns. When these records "
              "are scattered across spreadsheets or paper forms, staff can miss due dates, lose service history, or book a vehicle that is no longer available."),
        102: "1.2 Objectives",
        103: ("The project aims to provide a desktop workspace for maintaining vehicles and customers, creating rentals with a clear cost estimate, "
              "tracking active and overdue rentals, processing returns with odometer details, and reviewing revenue and audit activity."),
        104: "1.3 Problem Statement",
        105: ("The application must keep vehicle status and rental records consistent as bookings and returns occur. It must calculate discounts, tax, "
              "and late fees from stored rental information, retain customer history, and expose operational data through a usable interface."),
        106: "1.4 Scope and Challenges",
        107: ("FleetHub covers inventory, booking, return, customer history, service tracking, revenue summaries, CSV reporting, and audit logging. "
              "The implementation uses a local MySQL database and a Java desktop client; reliable operation therefore depends on the configured "
              "database service, schema migration, and JDBC credentials being available."),
        108: ("A key design challenge is keeping the user interface responsive and understandable while preserving data integrity across related "
              "vehicle, customer, and rental records. Checkout and return operations are grouped into explicit database transactions."),
        126: "LITERATURE SURVEY",
        127: "2.1 Desktop Interface",
        128: ("Java Swing provides standard desktop components, event handling, tables, and custom painting. FleetHub uses Swing panels, tables, "
              "dialogs, and custom-rendered cards to organize fleet tasks in one application window [1]."),
        129: "2.2 Relational Data and JDBC",
        130: ("A relational model represents vehicles, customers, rentals, users, and audit events as related records. MySQL Connector/J implements "
              "the JDBC interface used by a Java application to communicate with MySQL [4]. FleetDAO uses PreparedStatement parameters for data "
              "operations, keeping query values separate from SQL text [2]."),
        131: "2.3 Transaction Reliability",
        132: ("A rental checkout changes more than one record: it creates a rental and marks a vehicle as rented. Treating these changes as one "
              "transaction prevents a partial booking. MySQL InnoDB provides transaction commit and rollback behavior consistent with the ACID model [3]."),
        133: "2.4 Authentication and Auditability",
        134: ("Role records distinguish ADMIN and STAFF permissions, while audit rows provide a chronological record of important actions. The "
              "application stores a SHA-256 digest for seeded account passwords using Java's MessageDigest API [5]. Production deployments should "
              "use a salted, adaptive password hashing scheme and rotate sample credentials."),
        135: "2.5 Summary",
        136: ("These technologies support a layered desktop application: Swing presents the workflows, the DAO layer owns SQL operations, JDBC "
              "provides connectivity, and MySQL stores persistent records. The design reduces UI-to-database coupling and makes transactional "
              "operations explicit."),
        161: "REQUIREMENTS",
        163: "3.1 Functional Requirement Analysis",
        165: "The system is designed to support the following user tasks:",
        166: "1. Authenticate users and apply ADMIN or STAFF access at application launch.",
        167: "2. Search and filter fleet inventory by status and review odometer and service information.",
        168: "3. Create a rental for an existing customer and an available vehicle, with rental days, promo discount, tax, total, and expected return date.",
        169: "4. View active and overdue rentals, then complete returns using an ending odometer and overdue-fee calculation.",
        170: "5. Maintain customer records and inspect each customer's rental history.",
        171: "6. Review category revenue, fleet utilization, audit logs, and export rental records to CSV.",
        173: ("The data layer must preserve referential integrity and use atomic updates for checkout and return. The interface must provide clear "
              "feedback, role-aware controls, and readable status indicators."),
        175: "3.2 Software Requirements",
        177: "The implementation uses the following software components:",
        178: "Java desktop client:",
        179: "Core components:",
        180: "Java SE and Swing for the application window, dialogs, event handling, and custom-rendered components.",
        181: "JTable and table models for vehicle, rental, customer, and audit data.",
        182: "MySQL for persistent fleet, customer, rental, user, and audit records.",
        183: "MySQL Connector/J for JDBC connectivity; the driver is included through the project's lib folder.",
        184: "PreparedStatement queries and explicit commit/rollback handling for database changes.",
        185: "Graphics2D painting for rounded cards, status badges, revenue bars, and fleet utilization charts.",
        186: "CSV export for rental reports and HTML/TXT output for booking and return receipts.",
        187: "config.properties for database URL, username, and password, with local fallback settings.",
        188: "A standard JDK compiler and runtime; no third-party chart or UI library is required.",
        196: "3.3 Data and Operational Requirements",
        197: "The database stores the following operational information:",
        198: "Vehicles: model, category, daily rate, status, fuel type, odometer, and service interval.",
        199: "Customers: full name, phone number, and a unique license number.",
        201: "Rental and integrity rules:",
        202: "Rentals retain start and expected return times, days, charges, discount, tax, late fee, and odometer readings.",
        203: "Foreign keys link each rental to a customer and vehicle; transactions coordinate rental and vehicle status changes.",
        228: "ARCHITECTURE AND DESIGN",
        230: "4.1 System Architecture",
        232: "FleetHub separates presentation, database operations, and persistence into four connected components:",
        234: "Figure 4.1  FleetHub desktop, DAO, JDBC, and MySQL architecture",
        235: "Presentation layer: MainAppFrame and LoginDialog provide the user workflows and role-aware navigation.",
        236: "DAO layer: FleetDAO owns prepared queries, customer and vehicle operations, rental transactions, and reports.",
        237: "Connectivity layer: DBConnection reads config.properties and opens a MySQL Connector/J connection.",
        238: "Persistence layer: MySQL stores vehicles, customers, rentals, user roles, and audit logs.",
        240: ("The request flow begins with a user action, passes through validation and DAO operations, and reaches MySQL through JDBC. "
              "Transactional operations commit when all related updates succeed and roll back when a step fails."),
        250: "IMPLEMENTATION",
        252: "5.1 Database Design",
        254: "The core entities and their principal fields are summarized in Table 5.1.",
        256: "Table 5.1  FleetHub data model and integrity responsibilities",
        257: "5.2 Configuration and Migration",
        258: ("DBConnection loads db.url, db.username, and db.password from config.properties in the project root. If the file is missing, the "
              "connection factory retains the existing local defaults. Database credentials are environment-specific and are not included in this report."),
        259: ("AlterDB adds rental charge and odometer fields, vehicle service fields, expands the vehicle status width, creates users and audit_logs "
              "tables, and seeds ADMIN and STAFF accounts using SHA-256 digests. Its column checks make the additive migration safe to re-run."),
        260: "5.3 Rental Transactions",
        261: ("Checkout records vehicle, customer, rental days, total cost, discount, tax, start odometer, expected return, and Active status. The "
              "DAO then changes the selected vehicle from Available to Rented in the same transaction."),
        262: ("If the vehicle is no longer available or a database statement fails, the transaction is rolled back. This protects the relationship "
              "between the rental record and the vehicle's current status."),
        263: ("Return processing reads the expected return date, daily rate, and service interval. When a return is late, the fee is calculated at "
              "1.5 times the daily rate for each overdue day. The transaction completes the rental, stores the ending odometer and fee, updates "
              "vehicle mileage, and marks the vehicle Available or Service Due."),
        264: "5.4 Authentication and Audit Log",
        265: ("authenticateUser compares the SHA-256 digest of the submitted password with the stored digest and returns the user's role. The interface "
              "uses that role to restrict rate editing to ADMIN."),
        266: ("logActivity records username, role, action type, details, and creation time. The audit view displays the latest events so operational "
              "changes can be reviewed."),
        267: "5.5 Interface and Reports",
        268: ("The CozyTheme palette and reusable ModernComponents provide rounded cards, hover-aware buttons, status badges, and transient toast "
              "notifications. Custom Graphics2D panels draw revenue-by-category bars and a fleet utilization donut without an external chart library."),
        269: ("The booking view calculates the base subtotal, FLEET10 or CORP15 discount, 18% GST, final payable amount, and expected return date "
              "before confirmation. ReceiptDialog can save booking and return receipts as HTML or TXT; analytics can export rentals as CSV."),
        270: ("The six navigation views cover fleet inventory, booking, active and overdue rentals, customer history, business analytics, and the "
              "system audit log. Keyboard shortcuts support inventory search and audit refresh."),
        274: "RESULTS AND DISCUSSION",
        276: "6.1 Build and Workflow Verification",
        278: ("The Java source set compiled successfully with the standard JDK and the project's lib classpath. The dashboard and login flow launch. "
              "A live Java-to-MySQL integration check was not confirmed during report preparation because the configured JDBC connection did not "
              "establish successfully; database-dependent behavior must be verified with the local MySQL credentials and migrated schema."),
        280: "Figure 6.1  Booking estimate and atomic checkout workflow",
        288: "Figure 6.2  Return, late-fee, and service-status workflow",
        300: "6.2 Dashboard and Reporting Views",
        302: ("The dashboard groups fleet, rental, customer, analytics, and audit tasks. The booking estimate is calculated before confirmation; "
              "the DAO is responsible for persisting a confirmed rental as one database transaction."),
        304: "Figure 6.3  FleetHub dashboard view map and KPI summary",
        305: ("The interface includes total-fleet, available-now, active-lease, and completed-revenue KPIs. Status badges distinguish availability, "
              "rental, maintenance, service-due, and overdue states. Customer history and audit records support follow-up and review."),
        306: "6.3 Verification Summary",
        307: "Compile check: PASS - all Java source files compiled with the JDK and lib/* classpath.",
        308: "Database migration: NOT VERIFIED - the Java connection attempt did not reach a successful migration result.",
        309: "Database-backed booking, return, revenue, and audit checks: PENDING a successful local MySQL connection.",
        314: "CONCLUSION",
        315: ("FleetHub Management brings fleet inventory, rentals, customer records, service tracking, reports, and audit activity into one Java Swing "
              "application backed by MySQL."),
        316: ("The DAO architecture keeps SQL operations separate from interface components. Explicit transactions coordinate vehicle and rental changes "
              "during checkout and return, reducing the risk of inconsistent state."),
        317: ("The source compiles successfully and includes the requested workflow views and reporting components. Live database behavior remains "
              "dependent on the local MySQL service, valid configuration properties, and successful schema migration."),
        318: ("Future work should verify the application against the target MySQL instance, improve password storage with a salted adaptive hash, "
              "add automated transaction and boundary tests, and expand operational reporting."),
        359: "REFERENCES",
        360: ("[1] Oracle. \"Getting Started with Swing.\" The Java Tutorials. https://docs.oracle.com/javase/tutorial/uiswing/start/\n"
              "[2] Oracle. \"PreparedStatement.\" Java SE 25 API. https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html\n"
              "[3] Oracle. \"JTable.\" The Java Tutorials. https://docs.oracle.com/javase/tutorial/uiswing/components/table.html"),
        361: ("[4] Oracle. \"MessageDigest.\" Java SE 25 API. https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html\n"
              "[5] Oracle. \"InnoDB and the ACID Model.\" MySQL 8.0 Reference Manual. https://dev.mysql.com/doc/refman/8.0/en/mysql-acid.html\n"
              "[6] Oracle. \"MySQL Connector/J Developer Guide.\" https://dev.mysql.com/doc/connectors/en/connector-j.html"),
    }
    for idx, text in edits.items():
        if idx == 9:
            continue
        replace_text(p[idx], text)
    replace_text(p[9], "SOURADEEP PAUL (RA2511027010098)", format_from=p[8])

    # Update the existing static table of contents without changing its grid.
    toc = doc.tables[1]
    toc_rows = [
        ("CHAPTER NO", "CONTENTS", "PAGE NO"),
        ("1", "INTRODUCTION", ""),
        ("", "1.1 Motivation", ""),
        ("", "1.2 Objectives", ""),
        ("", "1.3 Problem Statement", ""),
        ("", "1.4 Scope and Challenges", ""),
        ("2", "LITERATURE SURVEY", ""),
        ("3", "REQUIREMENTS", ""),
        ("4", "ARCHITECTURE AND DESIGN", ""),
        ("5", "IMPLEMENTATION", ""),
        ("6", "RESULTS AND DISCUSSION", ""),
        ("7", "CONCLUSION", ""),
        ("8", "REFERENCES", ""),
    ]
    for ri, values in enumerate(toc_rows):
        for ci, value in enumerate(values):
            set_cell(toc.rows[ri].cells[ci], value)

    schema_rows = [
        ("ENTITY / AREA", "IMPORTANT FIELDS", "PURPOSE"),
        ("vehicles", "vehicle_id, model_name, category, daily_rate, status", "Fleet, rates, and availability"),
        ("customers", "customer_id, full_name, phone_number, license_number", "Customer identity and contact"),
        ("rentals", "rental_id, vehicle_id, customer_id, dates, status", "Booking and return lifecycle"),
        ("rental charges", "total_cost, discount_amount, tax_amount, late_fee", "Invoice and overdue charges"),
        ("service tracking", "odometer_km, service_interval_km, last_service_odometer", "Mileage and maintenance status"),
        ("users", "username, password_hash, role", "ADMIN / STAFF authentication"),
        ("audit_logs", "username, role, action_type, details, created_at", "Chronological activity history"),
        ("integrity", "primary keys, foreign keys, unique license", "Consistent linked records"),
    ]
    schema = doc.tables[2]
    for ri, values in enumerate(schema_rows):
        for ci, value in enumerate(values):
            set_cell(schema.rows[ri].cells[ci], value)

    doc.core_properties.title = "FleetHub Management Project Report"
    doc.core_properties.subject = "Java Swing and MySQL fleet lifecycle management application"
    doc.core_properties.author = "MVR TARUN; PRANAVA SAHITH; SOURADEEP PAUL"
    doc.core_properties.keywords = "FleetHub, Java Swing, MySQL, DAO, Fleet Management"
    doc.save(str(TEMP_DOCX))


def package_output(diagrams):
    with ZipFile(TEMP_DOCX, "r") as working:
        replacement = {
            "word/document.xml": working.read("word/document.xml"),
            "docProps/core.xml": working.read("docProps/core.xml"),
            "word/media/image2.png": Path(diagrams[0]).read_bytes(),
            "word/media/image3.png": Path(diagrams[1]).read_bytes(),
            "word/media/image4.png": Path(diagrams[2]).read_bytes(),
            "word/media/image5.png": Path(diagrams[3]).read_bytes(),
        }
    with ZipFile(TEMPLATE, "r") as source, ZipFile(TMP_ZIP, "w", ZIP_DEFLATED) as target:
        for info in source.infolist():
            data = replacement.get(info.filename)
            if data is None:
                data = source.read(info.filename)
            target.writestr(info, data, compress_type=info.compress_type)
    shutil.move(str(TMP_ZIP), str(OUTPUT))


def verify_package():
    with ZipFile(TEMPLATE, "r") as source, ZipFile(OUTPUT, "r") as result:
        src_names, out_names = set(source.namelist()), set(result.namelist())
        if src_names != out_names:
            raise RuntimeError("Package part inventory changed")
        changed = [name for name in sorted(src_names) if sha256(source.read(name)).digest() != sha256(result.read(name)).digest()]
        expected = {"word/document.xml", "docProps/core.xml", "word/media/image2.png", "word/media/image3.png", "word/media/image4.png", "word/media/image5.png"}
        if set(changed) != expected:
            raise RuntimeError(f"Unexpected package changes: {changed}")
    doc = Document(str(OUTPUT))
    if len(doc.sections) != 4 or len(doc.tables) != 3:
        raise RuntimeError("Template section or table structure changed")
    image_relationships = [rel for rel in doc.part.rels.values() if rel.reltype.endswith("/image")]
    if len(image_relationships) != 5:
        raise RuntimeError("Template image placement count changed")
    text = "\n".join(x.text for x in doc.paragraphs)
    for stale in ["ADHIRAJ PRATAP SINGH", "MUKKAMALLA SUBRAMANYA", "Cisco Packet Tracer", "e-commerce server"]:
        if stale in text:
            raise RuntimeError(f"Old-template text remains: {stale}")
    print("OUTPUT", OUTPUT)
    print("CHANGED PACKAGE PARTS", sorted(expected))
    print("PRESERVED PACKAGE PARTS", len(src_names - expected))
    print("SECTIONS", len(doc.sections), "TABLES", len(doc.tables), "INLINE FIGURES", len(doc.inline_shapes))
    print("REPORT WORDS", len(text.split()))


if __name__ == "__main__":
    diagrams_dir = WORK / "fleet_hub_figures"
    diagrams_dir.mkdir(parents=True, exist_ok=True)
    paths = [
        diagrams_dir / "architecture.png",
        diagrams_dir / "booking_flow.png",
        diagrams_dir / "return_flow.png",
        diagrams_dir / "dashboard_map.png",
    ]
    architecture_diagram(paths[0])
    square_flow(paths[1], "BOOKING WORKFLOW", "Estimate first, then commit the rental", [
        ("Choose customer", "Select an existing customer record."),
        ("Choose vehicle", "Only vehicles with Available status are offered."),
        ("Set rental terms", "Choose days; FLEET10 or CORP15 applies a discount."),
        ("Review invoice", "Base subtotal, discount, 18% GST, total, expected return."),
    ], "Confirm booking: insert rental + mark vehicle Rented in one transaction.", SAGE)
    square_flow(paths[2], "RETURN WORKFLOW", "Close the rental and update vehicle service state", [
        ("Record odometer", "Enter the vehicle's ending odometer reading."),
        ("Check due date", "Compare return time with the expected return timestamp."),
        ("Calculate late fee", "Overdue days x daily rate x 1.5, when overdue."),
        ("Update both records", "Complete rental; update mileage and availability or Service Due."),
    ], "The rental and vehicle updates commit together; an error rolls the work back.", TERRA)
    dashboard_map(paths[3])
    make_content()
    package_output(paths)
    verify_package()
