//
//  HomeViewController.m
//  Hang
//
//  Created by Girum on 4/27/13.
//  Copyright (c) 2013 Girum. All rights reserved.
//

#import "HomeViewController.h"

@interface HomeViewController ()

@property (weak, nonatomic) IBOutlet UIProgressView *progressBar;

@property (strong, nonatomic) NSMutableArray *incomingBroadcasts;

@end

@implementation HomeViewController

@synthesize incomingBroadcasts = _incomingBroadcasts;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.progressBar.progressTintColor = [UIColor greenColor];
    self.progressBar.trackTintColor = [UIColor grayColor];
    
    self.incomingBroadcasts = [[NSMutableArray alloc] initWithCapacity:10];
    [self.incomingBroadcasts addObject:@"Girum"];
    [self.incomingBroadcasts addObject:@"Nati"];
    [self.incomingBroadcasts addObject:@"Samora"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
