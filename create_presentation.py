import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

def build_presentation():
    prs = Presentation()
    # 16:9 widescreen
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6] # blank

    # Color Palette - Modern Dark Obsidian & Teal/Emerald
    BG_COLOR = RGBColor(15, 23, 42)          # Deep Slate #0F172A
    CARD_BG = RGBColor(30, 41, 59)           # Card Slate #1E293B
    CARD_BORDER = RGBColor(51, 65, 85)       # Border Slate #334155
    ACCENT_TEAL = RGBColor(20, 184, 166)     # Teal #14B8A6
    ACCENT_EMERALD = RGBColor(16, 185, 129)  # Emerald #10B981
    ACCENT_AMBER = RGBColor(245, 158, 11)    # Amber #F59E0B
    ACCENT_BLUE = RGBColor(59, 130, 246)     # Sky Blue #3B82F6
    TEXT_LIGHT = RGBColor(248, 250, 252)     # White #F8FAFC
    TEXT_MUTED = RGBColor(148, 163, 184)     # Silver #94A3B8
    TEXT_BODY = RGBColor(226, 232, 240)      # Light Slate #E2E8F0

    def set_slide_background(slide):
        bg_shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, Inches(13.333), Inches(7.5))
        bg_shape.fill.solid()
        bg_shape.fill.fore_color.rgb = BG_COLOR
        bg_shape.line.fill.background() # no line
        return bg_shape

    def add_header(slide, category_text, title_text, category_color=ACCENT_TEAL):
        # Category Badge
        cat_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.4), Inches(11.7), Inches(0.4))
        tf_cat = cat_box.text_frame
        tf_cat.word_wrap = True
        tf_cat.margin_left = tf_cat.margin_right = tf_cat.margin_top = tf_cat.margin_bottom = 0
        p_cat = tf_cat.paragraphs[0]
        p_cat.text = category_text.upper()
        p_cat.font.size = Pt(11)
        p_cat.font.bold = True
        p_cat.font.color.rgb = category_color
        p_cat.font.name = "Segoe UI"

        # Main Title
        title_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.72), Inches(11.7), Inches(0.65))
        tf_title = title_box.text_frame
        tf_title.word_wrap = True
        tf_title.margin_left = tf_title.margin_right = tf_title.margin_top = tf_title.margin_bottom = 0
        p_title = tf_title.paragraphs[0]
        p_title.text = title_text
        p_title.font.size = Pt(24)
        p_title.font.bold = True
        p_title.font.color.rgb = TEXT_LIGHT
        p_title.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 1: App Title & Aesthetic Theme
    # =========================================================================
    s1 = prs.slides.add_slide(blank_layout)
    set_slide_background(s1)

    # Decorative background glows / card
    title_card = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.2), Inches(1.1), Inches(10.933), Inches(5.3))
    title_card.fill.solid()
    title_card.fill.fore_color.rgb = CARD_BG
    title_card.line.color.rgb = CARD_BORDER
    title_card.line.width = Pt(1.5)

    # Top Pill Badge
    badge = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(4.7), Inches(1.6), Inches(3.933), Inches(0.45))
    badge.fill.solid()
    badge.fill.fore_color.rgb = RGBColor(19, 78, 74) # Dark Teal
    badge.line.color.rgb = ACCENT_TEAL
    badge.line.width = Pt(1)
    tf_b = badge.text_frame
    tf_b.margin_top = Inches(0.05)
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    p_b.text = "SMART VEHICLE & FLEET MANAGEMENT"
    p_b.font.size = Pt(10)
    p_b.font.bold = True
    p_b.font.color.rgb = RGBColor(94, 234, 212)
    p_b.font.name = "Segoe UI"

    # App Main Title
    t_box = s1.shapes.add_textbox(Inches(1.6), Inches(2.3), Inches(10.133), Inches(1.2))
    tf_t = t_box.text_frame
    tf_t.word_wrap = True
    p_t = tf_t.paragraphs[0]
    p_t.alignment = PP_ALIGN.CENTER
    p_t.text = "FleetWise Hub"
    p_t.font.size = Pt(46)
    p_t.font.bold = True
    p_t.font.color.rgb = TEXT_LIGHT
    p_t.font.name = "Segoe UI"

    # Subtitle
    sub_box = s1.shapes.add_textbox(Inches(1.8), Inches(3.5), Inches(9.733), Inches(0.8))
    tf_sub = sub_box.text_frame
    tf_sub.word_wrap = True
    p_sub = tf_sub.paragraphs[0]
    p_sub.alignment = PP_ALIGN.CENTER
    p_sub.text = "Next-Generation Intelligent Rental Automation, Inventory Tracking & Business Analytics"
    p_sub.font.size = Pt(17)
    p_sub.font.color.rgb = TEXT_MUTED
    p_sub.font.name = "Segoe UI"

    # 3 Feature Pills at the bottom of Title
    features = [
        ("JAVA SWING GUI", "Cozy Ergonomic Theme", ACCENT_TEAL),
        ("MYSQL RDBMS", "Normalized Relational DB", ACCENT_AMBER),
        ("DAO ARCHITECTURE", "ACID Transaction Safe", ACCENT_BLUE)
    ]
    pill_w = Inches(3.1)
    pill_gap = Inches(0.4)
    start_x = Inches(1.6 + 0.3)
    for i, (f_title, f_sub, f_col) in enumerate(features):
        px = start_x + i * (pill_w + pill_gap)
        p_shape = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, px, Inches(4.7), pill_w, Inches(1.1))
        p_shape.fill.solid()
        p_shape.fill.fore_color.rgb = RGBColor(20, 27, 45)
        p_shape.line.color.rgb = f_col
        p_shape.line.width = Pt(1)
        
        ptf = p_shape.text_frame
        ptf.word_wrap = True
        ptf.margin_top = Inches(0.2)
        pp1 = ptf.paragraphs[0]
        pp1.alignment = PP_ALIGN.CENTER
        pp1.text = f_title
        pp1.font.size = Pt(13)
        pp1.font.bold = True
        pp1.font.color.rgb = f_col
        pp1.font.name = "Segoe UI"
        
        pp2 = ptf.add_paragraph()
        pp2.alignment = PP_ALIGN.CENTER
        pp2.text = f_sub
        pp2.font.size = Pt(10.5)
        pp2.font.color.rgb = TEXT_MUTED
        pp2.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 2: App Description (FleetWise Hub)
    # =========================================================================
    s2 = prs.slides.add_slide(blank_layout)
    set_slide_background(s2)
    add_header(s2, "Application Overview & Architecture", "FleetWise Hub: System Description & Design", ACCENT_TEAL)

    # Left Column: Overview Summary Card
    left_card = s2.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(1.5), Inches(4.3), Inches(5.4))
    left_card.fill.solid()
    left_card.fill.fore_color.rgb = CARD_BG
    left_card.line.color.rgb = CARD_BORDER
    left_card.line.width = Pt(1)

    l_tf = left_card.text_frame
    l_tf.word_wrap = True
    l_tf.margin_left = l_tf.margin_right = l_tf.margin_top = Inches(0.35)
    
    lp1 = l_tf.paragraphs[0]
    lp1.text = "About FleetWise Hub"
    lp1.font.size = Pt(18)
    lp1.font.bold = True
    lp1.font.color.rgb = ACCENT_TEAL
    lp1.font.name = "Segoe UI"

    lp2 = l_tf.add_paragraph()
    lp2.space_before = Pt(14)
    lp2.text = "FleetWise Hub is an enterprise-grade desktop vehicle rental and fleet lifecycle management system. It bridges a modern Java Swing graphical user interface with an ACID-compliant MySQL database."
    lp2.font.size = Pt(13)
    lp2.font.color.rgb = TEXT_BODY
    lp2.font.name = "Segoe UI"

    lp3 = l_tf.add_paragraph()
    lp3.space_before = Pt(14)
    lp3.text = "Key Project Purpose:"
    lp3.font.size = Pt(14)
    lp3.font.bold = True
    lp3.font.color.rgb = TEXT_LIGHT
    lp3.font.name = "Segoe UI"

    lp4 = l_tf.add_paragraph()
    lp4.space_before = Pt(6)
    lp4.text = "• Eliminate manual spreadsheet overhead with real-time operational tables.\n• Provide strict transactional integrity during rental checkouts & returns.\n• Deliver instant business visibility through live billing and revenue aggregation."
    lp4.font.size = Pt(12.5)
    lp4.font.color.rgb = TEXT_MUTED
    lp4.font.name = "Segoe UI"

    # Right Column: 4 Architecture Feature Cards
    pillars = [
        ("Layered DAO Pattern Architecture", 
         "Strict decoupling of UI and database logic. FleetDAO encapsulates all SQL statements, connection management, and transaction boundaries, guaranteeing clean code maintainability.",
         ACCENT_TEAL),
        ("Real-Time Fleet State Synchronization", 
         "Tracks vehicle operational statuses ('Available', 'Rented', 'Maintenance') in real-time, preventing double bookings and reflecting status transitions instantly across UI tables.",
         ACCENT_EMERALD),
        ("Atomic Multi-Table Transactions (ACID)", 
         "Encapsulates vehicle rental and return procedures inside database transactions with auto-commit disabled, ensuring rollback safety if any step fails.",
         ACCENT_AMBER),
        ("Dynamic Financial Analytics & Billing", 
         "Calculates total billing amounts automatically based on rental durations and daily vehicle rates, while aggregating lifetime completed revenues dynamically.",
         ACCENT_BLUE)
    ]

    card_h = Inches(1.22)
    card_gap = Inches(0.17)
    for i, (p_title, p_desc, p_col) in enumerate(pillars):
        cy = Inches(1.5) + i * (card_h + card_gap)
        c_shape = s2.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(5.3), cy, Inches(7.2), card_h)
        c_shape.fill.solid()
        c_shape.fill.fore_color.rgb = CARD_BG
        c_shape.line.color.rgb = CARD_BORDER
        c_shape.line.width = Pt(1)

        ctf = c_shape.text_frame
        ctf.word_wrap = True
        ctf.margin_left = ctf.margin_right = Inches(0.28)
        ctf.margin_top = Inches(0.15)
        
        cp1 = ctf.paragraphs[0]
        cp1.text = f"✔  {p_title}"
        cp1.font.size = Pt(13)
        cp1.font.bold = True
        cp1.font.color.rgb = p_col
        cp1.font.name = "Segoe UI"

        cp2 = ctf.add_paragraph()
        cp2.space_before = Pt(4)
        cp2.text = p_desc
        cp2.font.size = Pt(10.5)
        cp2.font.color.rgb = TEXT_BODY
        cp2.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 3: GUI Screenshot & 3 Detailed Points
    # =========================================================================
    s3 = prs.slides.add_slide(blank_layout)
    set_slide_background(s3)
    add_header(s3, "Live Application GUI & Interactive Features", "FleetWise Hub: Graphical User Interface", ACCENT_TEAL)

    # GUI Screenshot on Left
    gui_img_path = "fleetwise_gui_screenshot.png"
    if os.path.exists(gui_img_path):
        s3.shapes.add_picture(gui_img_path, Inches(0.8), Inches(1.5), width=Inches(6.4))

    # Right side: 3 Points Cards as requested
    gui_points = [
        ("1. Cozy Modern UI & Tabbed Workflow",
         "The GUI employs a specialized CozyTheme palette (warm cream backgrounds, slate accents, and sage-green selections) with a multi-tabbed layout organizing Fleet Inventory, Vehicle Rental, Active Rentals, and Analytics.",
         ACCENT_TEAL),
        ("2. Real-Time Interactive Inventory Table",
         "Presents live fleet specifications including vehicle IDs, models (Camry, Tesla, Explorer, Pulsar), categories, daily rental rates, current statuses, and fuel types, with centered typography and zebra-style borders.",
         ACCENT_EMERALD),
        ("3. Action-Oriented Fleet Control & Live Refresh",
         "Equipped with dedicated action buttons ('Add Vehicle' and 'Refresh') enabling operators to register new fleet additions on the fly and immediately re-sync data directly from the active MySQL database.",
         ACCENT_AMBER)
    ]

    p_card_h = Inches(1.65)
    p_gap = Inches(0.22)
    for i, (pt_title, pt_desc, pt_col) in enumerate(gui_points):
        py = Inches(1.5) + i * (p_card_h + p_gap)
        pt_card = s3.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(7.45), py, Inches(5.08), p_card_h)
        pt_card.fill.solid()
        pt_card.fill.fore_color.rgb = CARD_BG
        pt_card.line.color.rgb = pt_col
        pt_card.line.width = Pt(1.2)

        pt_tf = pt_card.text_frame
        pt_tf.word_wrap = True
        pt_tf.margin_left = pt_tf.margin_right = Inches(0.28)
        pt_tf.margin_top = Inches(0.18)

        pp1 = pt_tf.paragraphs[0]
        pp1.text = pt_title
        pp1.font.size = Pt(13.5)
        pp1.font.bold = True
        pp1.font.color.rgb = pt_col
        pp1.font.name = "Segoe UI"

        pp2 = pt_tf.add_paragraph()
        pp2.space_before = Pt(6)
        pp2.text = pt_desc
        pp2.font.size = Pt(11)
        pp2.font.color.rgb = TEXT_BODY
        pp2.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 4: SQL Code Screenshot 1 - DDL Schema
    # =========================================================================
    s4 = prs.slides.add_slide(blank_layout)
    set_slide_background(s4)
    add_header(s4, "Database Architecture & Relational Schema", "MySQL DDL: Table Definitions & Integrity Constraints", ACCENT_TEAL)

    # Screenshot on Left
    s4_img = "sql_ddl_screenshot.png"
    if os.path.exists(s4_img):
        s4.shapes.add_picture(s4_img, Inches(0.8), Inches(1.45), width=Inches(5.7))

    # Points on Right (3-4 points)
    ddl_points = [
        ("Normalized Relational Schema (3NF)",
         "Decomposes vehicle fleet, customer data, and rental leases into three decoupled entities (vehicles, customers, rentals) to ensure data normalization and zero redundancy.",
         ACCENT_TEAL),
        ("Foreign Key Referential Integrity",
         "Enforces relational integrity via FOREIGN KEY (vehicle_id) REFERENCES vehicles and FOREIGN KEY (customer_id) REFERENCES customers, blocking orphaned records.",
         ACCENT_EMERALD),
        ("Strict Data Types & Field Constraints",
         "Employs DECIMAL(15,2) for exact monetary rates, TIMESTAMP DEFAULT CURRENT_TIMESTAMP for automated checkout auditing, and UNIQUE constraint on driver license IDs.",
         ACCENT_AMBER),
        ("Surrogate Primary Key Indexing",
         "Utilizes auto-incrementing integer primary keys across all tables, establishing fast clustered indexing and highly efficient join lookups across the database.",
         ACCENT_BLUE)
    ]

    s4_h = Inches(1.22)
    s4_gap = Inches(0.17)
    for i, (title, desc, col) in enumerate(ddl_points):
        sy = Inches(1.45) + i * (s4_h + s4_gap)
        sc = s4.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(6.75), sy, Inches(5.78), s4_h)
        sc.fill.solid()
        sc.fill.fore_color.rgb = CARD_BG
        sc.line.color.rgb = CARD_BORDER
        sc.line.width = Pt(1)

        sctf = sc.text_frame
        sctf.word_wrap = True
        sctf.margin_left = sctf.margin_right = Inches(0.25)
        sctf.margin_top = Inches(0.14)

        sp1 = sctf.paragraphs[0]
        sp1.text = f"•  {title}"
        sp1.font.size = Pt(12.5)
        sp1.font.bold = True
        sp1.font.color.rgb = col
        sp1.font.name = "Segoe UI"

        sp2 = sctf.add_paragraph()
        sp2.space_before = Pt(3)
        sp2.text = desc
        sp2.font.size = Pt(10.5)
        sp2.font.color.rgb = TEXT_BODY
        sp2.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 5: SQL Code Screenshot 2 - DML Seeding & Real-Time Queries
    # =========================================================================
    s5 = prs.slides.add_slide(blank_layout)
    set_slide_background(s5)
    add_header(s5, "Data Seeding & Application Query Operations", "MySQL DML: Initial Seeding & Dynamic Live Queries", ACCENT_TEAL)

    s5_img = "sql_dml_screenshot.png"
    if os.path.exists(s5_img):
        s5.shapes.add_picture(s5_img, Inches(0.8), Inches(1.5), width=Inches(5.7))

    dml_points = [
        ("Multi-Tier Fleet Dataset Seeding",
         "Initializes diverse vehicle categories (Sedan, SUV, EV, Gasoline, Hybrid) with daily rates and test customers to enable turnkey testing and demonstration.",
         ACCENT_TEAL),
        ("Filtered Available Vehicle Lookups",
         "Executes SELECT * FROM vehicles WHERE status = 'Available' to populate the rental dropdown and table, preventing customers from selecting reserved vehicles.",
         ACCENT_EMERALD),
        ("Live Active Rental Monitoring",
         "Queries SELECT ... FROM rentals WHERE status = 'Active' to feed the Active Rentals tab, providing real-time visibility into customer IDs, durations, and revenue.",
         ACCENT_AMBER),
        ("Parameterized PreparedStatements",
         "FleetDAO prepares all SQL parameters securely using JDBC PreparedStatement, guaranteeing protection against SQL injection and ensuring high query cache reuse.",
         ACCENT_BLUE)
    ]

    for i, (title, desc, col) in enumerate(dml_points):
        sy = Inches(1.5) + i * (s4_h + s4_gap)
        sc = s5.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(6.75), sy, Inches(5.78), s4_h)
        sc.fill.solid()
        sc.fill.fore_color.rgb = CARD_BG
        sc.line.color.rgb = CARD_BORDER
        sc.line.width = Pt(1)

        sctf = sc.text_frame
        sctf.word_wrap = True
        sctf.margin_left = sctf.margin_right = Inches(0.25)
        sctf.margin_top = Inches(0.14)

        sp1 = sctf.paragraphs[0]
        sp1.text = f"•  {title}"
        sp1.font.size = Pt(12.5)
        sp1.font.bold = True
        sp1.font.color.rgb = col
        sp1.font.name = "Segoe UI"

        sp2 = sctf.add_paragraph()
        sp2.space_before = Pt(3)
        sp2.text = desc
        sp2.font.size = Pt(10.5)
        sp2.font.color.rgb = TEXT_BODY
        sp2.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 6: SQL Code Screenshot 3 - ACID Transactions & Analytics
    # =========================================================================
    s6 = prs.slides.add_slide(blank_layout)
    set_slide_background(s6)
    add_header(s6, "Transaction Processing & Financial Analytics", "ACID Transactions & Aggregated Revenue Analytics", ACCENT_TEAL)

    s6_img = "sql_trans_screenshot.png"
    if os.path.exists(s6_img):
        s6.shapes.add_picture(s6_img, Inches(0.8), Inches(1.45), width=Inches(5.7))

    trans_points = [
        ("Atomic Booking State Transition",
         "Combines rental insertion and vehicle status update into an atomic transaction (conn.setAutoCommit(false)) ensuring a vehicle is never rented without an active lease.",
         ACCENT_TEAL),
        ("Two-Way Vehicle Return Synchronization",
         "Safely synchronizes rental completion and vehicle availability in a coordinated transaction, immediately re-enabling the vehicle for future customer bookings.",
         ACCENT_EMERALD),
        ("Automatic Failure Rollback",
         "Captures SQLExceptions and triggers conn.rollback() on unexpected connection or constraint failures, preventing partial state corruption or orphaned records.",
         ACCENT_AMBER),
        ("Aggregated Lifetime Revenue Metrics",
         "Runs SELECT SUM(total_cost) FROM rentals WHERE status = 'Completed' to compute total fleet income dynamically, displaying up-to-the-minute business earnings.",
         ACCENT_BLUE)
    ]

    for i, (title, desc, col) in enumerate(trans_points):
        sy = Inches(1.45) + i * (s4_h + s4_gap)
        sc = s6.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(6.75), sy, Inches(5.78), s4_h)
        sc.fill.solid()
        sc.fill.fore_color.rgb = CARD_BG
        sc.line.color.rgb = CARD_BORDER
        sc.line.width = Pt(1)

        sctf = sc.text_frame
        sctf.word_wrap = True
        sctf.margin_left = sctf.margin_right = Inches(0.25)
        sctf.margin_top = Inches(0.14)

        sp1 = sctf.paragraphs[0]
        sp1.text = f"•  {title}"
        sp1.font.size = Pt(12.5)
        sp1.font.bold = True
        sp1.font.color.rgb = col
        sp1.font.name = "Segoe UI"

        sp2 = sctf.add_paragraph()
        sp2.space_before = Pt(3)
        sp2.text = desc
        sp2.font.size = Pt(10.5)
        sp2.font.color.rgb = TEXT_BODY
        sp2.font.name = "Segoe UI"

    # =========================================================================
    # SLIDE 7: Thank You Slide
    # =========================================================================
    s7 = prs.slides.add_slide(blank_layout)
    set_slide_background(s7)

    # Thank you main container card
    ty_card = s7.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.5), Inches(1.1), Inches(10.333), Inches(5.3))
    ty_card.fill.solid()
    ty_card.fill.fore_color.rgb = CARD_BG
    ty_card.line.color.rgb = CARD_BORDER
    ty_card.line.width = Pt(1.5)

    # Thank you headline
    ty_tb = s7.shapes.add_textbox(Inches(2.0), Inches(1.8), Inches(9.333), Inches(1.2))
    tytf = ty_tb.text_frame
    tytf.word_wrap = True
    typ1 = tytf.paragraphs[0]
    typ1.alignment = PP_ALIGN.CENTER
    typ1.text = "Thank You!"
    typ1.font.size = Pt(50)
    typ1.font.bold = True
    typ1.font.color.rgb = ACCENT_TEAL
    typ1.font.name = "Segoe UI"

    # Sub-heading
    typ2 = tytf.add_paragraph()
    typ2.alignment = PP_ALIGN.CENTER
    typ2.space_before = Pt(8)
    typ2.text = "Questions & Interactive Discussion"
    typ2.font.size = Pt(20)
    typ2.font.color.rgb = TEXT_LIGHT
    typ2.font.name = "Segoe UI"

    # Summary Card inside Thank You
    summ_shape = s7.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(2.2), Inches(3.4), Inches(8.933), Inches(1.5))
    summ_shape.fill.solid()
    summ_shape.fill.fore_color.rgb = RGBColor(20, 27, 45)
    summ_shape.line.color.rgb = ACCENT_EMERALD
    summ_shape.line.width = Pt(1)

    stf = summ_shape.text_frame
    stf.word_wrap = True
    stf.margin_left = stf.margin_right = Inches(0.4)
    stf.margin_top = Inches(0.2)

    sp1 = stf.paragraphs[0]
    sp1.alignment = PP_ALIGN.CENTER
    sp1.text = "FleetWise Hub demonstrates robust, scalable, and responsive fleet management"
    sp1.font.size = Pt(14)
    sp1.font.bold = True
    sp1.font.color.rgb = RGBColor(94, 234, 212)
    sp1.font.name = "Segoe UI"

    sp2 = stf.add_paragraph()
    sp2.alignment = PP_ALIGN.CENTER
    sp2.space_before = Pt(6)
    sp2.text = "Engineered through clean Object-Oriented Java design, elegant Swing user experience, and ACID-compliant MySQL database transaction management."
    sp2.font.size = Pt(12)
    sp2.font.color.rgb = TEXT_MUTED
    sp2.font.name = "Segoe UI"

    # Footer note
    ft_tb = s7.shapes.add_textbox(Inches(2.0), Inches(5.2), Inches(9.333), Inches(0.6))
    ftf = ft_tb.text_frame
    ftp = ftf.paragraphs[0]
    ftp.alignment = PP_ALIGN.CENTER
    ftp.text = "FleetWise Hub • Java Swing & MySQL Fleet Management System"
    ftp.font.size = Pt(12)
    ftp.font.color.rgb = TEXT_MUTED
    ftp.font.name = "Segoe UI"

    # Save presentation
    output_filename = "FleetWise_Hub_Presentation.pptx"
    prs.save(output_filename)
    print(f"Presentation saved successfully as: {output_filename}")

if __name__ == "__main__":
    build_presentation()
