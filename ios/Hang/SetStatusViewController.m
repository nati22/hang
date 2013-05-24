//
//  SetStatusViewController.m
//  Hang
//
//  Created by Girum on 4/27/13.
//  Copyright (c) 2013 Girum. All rights reserved.
//

#import "SetStatusViewController.h"

@interface SetStatusViewController ()
@property (weak, nonatomic) IBOutlet UISlider *progressBar;

@end

@implementation SetStatusViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)cancel:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)done:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
